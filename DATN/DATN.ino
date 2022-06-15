#include <ESP8266WiFi.h>                                                    // thu vien esp8266 
#include <FirebaseArduino.h>                                                // thu vien firebase 
#include <DHT.h>                                                            // thu vien dht11 temperature and humidity sensor 
#include <NTPClient.h>
#include <WiFiUdp.h>
#include <LiquidCrystal_I2C.h>
#include <DNSServer.h>
#include <ESP8266WebServer.h>
#include <WiFiManager.h>         //https://github.com/tzapu/WiFiManager

//https:///
#define FIREBASE_HOST "khoaluantotnghiep-2b62f-default-rtdb.firebaseio.com"                          // ten host firebase
#define FIREBASE_AUTH "e7HwIw0KMLF9Ofzdr93mb0IOUqWfedTlcGgVzRZh"            // ma xac thuc firebase

//#define WIFI_SSID "Amazon Team"                      // ten WiFi 
//#define WIFI_PASSWORD "112358Luan"                      //mat khau WIFI
//Month names
int months[12]={1, 2, 3, 4, 5, 6,7,8,9,10,11,12};
int statusControlPump = 0 ;
WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP, "pool.ntp.org");

#define BUTTON 14
#define ROLE  D6
#define DHTPIN D4                 // Chân D4 trên board ESP8266 nối đến cảm biến DHT11
#define DHTTYPE DHT11             // Loại Cảm biến DHT11
DHT dht(DHTPIN, DHTTYPE);      
int count = 0 ;    
int statusOn ;   // bến này dùng để đánh dấu trạng thái On Off của bơm, statusOn = 0 : OFF , statusOn = 1: ON                                          
int real_value , value ;
int statusPrevious = -1 ;
LiquidCrystal_I2C lcd(0x27, 16, 2);

void setup() {
  Serial.begin(9600);
  pinMode(ROLE,OUTPUT);
  statusOn = 0; // mặc định ban đầu pump là OFF
  digitalWrite(ROLE,LOW);
  pinMode(BUTTON, INPUT_PULLUP);
  //WiFiManager
    WiFiManager wifiManager;
//    wifiManager.resetSettings();
    wifiManager.autoConnect("AutoConnectAP");

//  delay(1000);                
//  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);                                     //try to connect with wifi
//  Serial.print("Connecting to ");
//  Serial.print(WIFI_SSID);
//  while (WiFi.status() != WL_CONNECTED) {
//    Serial.print(".");
//    delay(500);
//  }
//  Serial.println();
//  Serial.print("Connected to ");
//  Serial.println(WIFI_SSID);
//  Serial.print("IP Address is : ");
//  Serial.println(WiFi.localIP());          // In ra địa chỉ IP mà con  ESP kết nối đến wifi
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);           // Kết nối đến firebase
  dht.begin();                                       //bắt đầu đọc dht11 sensor
  // Initialize a NTPClient to get time
  timeClient.begin();
  // Set offset time in seconds to adjust for your timezone, for example:
  // GMT +1 = 3600
  // GMT +8 = 28800
  // GMT -1 = -3600
  // GMT 0 = 0
  timeClient.setTimeOffset(25200);
  // initialize the LCD
  lcd.begin();
statusControlPump =0 ;
  // Turn on the blacklight and print a message.
  lcd.backlight();
  
  statusPrevious = -1 ;
  lcd.setCursor(10, 1);
  lcd.print("C:OFF");
}

void loop() { 

  timeClient.update();

  time_t epochTime = timeClient.getEpochTime();
//  Serial.print("Epoch Time: ");
//  Serial.println(epochTime);
//  
//  String formattedTime = timeClient.getFormattedTime();
//  Serial.print("Formatted Time: ");
//  Serial.println(formattedTime);  

  int buttonState = digitalRead(BUTTON);
//
//  if(buttonState == HIGH ){
//     Serial.println("Not Pressed");
//  }else {
//     Serial.println("Pressed");
//  }
  int currentHour = timeClient.getHours();
  Serial.print("Hour: ");
  Serial.println(currentHour);  

  int currentMinute = timeClient.getMinutes();
  Serial.print("Minutes: ");
  Serial.println(currentMinute);     

  //Get a time structure
  struct tm *ptm = gmtime ((time_t *)&epochTime); 

  int monthDay = ptm->tm_mday;
  Serial.print("Month day: ");
  Serial.println(monthDay);

  int currentMonth = ptm->tm_mon+1;
  Serial.print("Month: ");
  Serial.println(currentMonth);

  int currentMonthName = months[currentMonth-1];
  Serial.print("Month name: ");
  Serial.println(currentMonthName);

  int currentYear = ptm->tm_year+1900;
  Serial.print("Year: ");
  Serial.println(currentYear);

  //Print complete date:
  String currentDate = String(currentYear) + "-" + String(currentMonth) + "-" + String(monthDay)+"-"+String(currentHour)+"-"+String(currentMinute);
  Serial.print("Current date: ");
  Serial.println(currentDate);

  Serial.println("");

  /* Đọc giá trị nhiệt độ, đô ẩm không khí, độ ẩm đất và quy đổi về phần trăm*/
  float h = dht.readHumidity();        // Đọc độ ảm không khí
  float t = dht.readTemperature();     // Đọc nhiệt độ không khí
  for(int i = 0 ; i < 10 ; i++) { // Lấy 10 lần giá trị của cảm biến độ ẩm đất, sau đó lấy giá trị chung 
                                  //  bình để được giá trị chính xác nhất.
    real_value += analogRead(A0);
   // Serial.print( "A0 = ");
  //  Serial.println(analogRead(A0));
  }
 Serial.print("real_value : ");
 Serial.println(real_value);
  value = real_value/10;
  real_value = 0 ;
  int percent = map(value,360, 1024,0,100);// Đặt giá trị đầu và giá trị cuối để đưa về giá trị phần trăm
  percent = 100 - percent; // Quy đổi ra phân trăm

  if (isnan(h) || isnan(t)) {  // Kiểm tra việc đọc cảm biến nhiệt độ, độ ẩm có bị lỗi không
    h = 0 ;
    t = 0 ;
  }
  /*----------------In giá trị cảm biến lên màn hình Minitor*/
  Serial.print("Humidity: ");  Serial.print(h);
  String fireHumid = String(h) + String("%");                                         
  Serial.print("%  Temperature: ");  Serial.print(t);  Serial.println("°C ");
  String fireTemp = String(t) + String("°C"); 
  Serial.print("Moisture : ");
  Serial.println(percent)    ;
  
  /*------------------Gửi dữ liệu lên firebase----------------------------*/
  Firebase.setString("/list-devices/aKygDmC8JXNOtdR6iHSuVerHdvX2/sensor/Humidity/values", String(h)); // đẩy dữ liệu độ ẩm lên firebase của nhánh /list-devices/aKygDmC8JXNOtdR6iHSuVerHdvX2/sensor/Humidity/values
  Firebase.setString("/list-devices/aKygDmC8JXNOtdR6iHSuVerHdvX2/sensor/Temperature/values", String(t));// đẩy dữ liệu độ ẩm lên firebase của nhánh /list-devices/aKygDmC8JXNOtdR6iHSuVerHdvX2/sensor/Temperature/values
  Firebase.setString("/list-devices/aKygDmC8JXNOtdR6iHSuVerHdvX2/sensor/Moisture/values", String(percent));// đẩy dữ liệu độ ẩm lên firebase của nhánh /list-devices/aKygDmC8JXNOtdR6iHSuVerHdvX2/sensor/Moisture/values

  int numberKey = millis();
  
  Firebase.setString("/history/aKygDmC8JXNOtdR6iHSuVerHdvX2/list-device/Humidity/"+String(numberKey)+"/values", String(h)); 
  Firebase.setString("/history/aKygDmC8JXNOtdR6iHSuVerHdvX2/list-device/Temperature/"+String(numberKey)+"/values", String(t));
  Firebase.setString("/history/aKygDmC8JXNOtdR6iHSuVerHdvX2/list-device/Moisture/"+String(numberKey)+"/values", String(percent));
  Firebase.setString("/history/aKygDmC8JXNOtdR6iHSuVerHdvX2/list-device/Humidity/"+String(numberKey)+"/time", String(currentDate));
  Firebase.setString("/history/aKygDmC8JXNOtdR6iHSuVerHdvX2/list-device/Temperature/"+String(numberKey)+"/time", String(currentDate)); 
  Firebase.setString("/history/aKygDmC8JXNOtdR6iHSuVerHdvX2/list-device/Moisture/"+String(numberKey)+"/time", String(currentDate)); 

  int statusPump = Firebase.getInt("/list-devices-control/aKygDmC8JXNOtdR6iHSuVerHdvX2/devices-control/Pump/statusDevice"); // Đọc trạng thái bơm từ firebase
  Serial.println("pump = "+String(statusPump));
  if(statusPump == 1) {
        Serial.println("Phone Pressed");
//        digitalWrite(ROLE, HIGH);
//        lcd.print("C:ON");
        statusOn = 1;

  }else {
        Serial.println("Phone Pressed");
//        digitalWrite(ROLE, LOW);
//        lcd.print("C:OFF");
        statusOn = 0 ;
  }
  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("T:"+String(t));
  lcd.print((char)223);
  lcd.print("C");
   lcd.print(" ");
  lcd.print("M:"+String(percent)+"%");
  lcd.setCursor(0, 1);
  lcd.print("H:"+String(h) + "%");
 
  count++;
  Serial.print("Count : ");
  Serial.println(count);
 
//  statusPump = Firebase.getInt("/list-devices-control/aKygDmC8JXNOtdR6iHSuVerHdvX2/devices-control/Pump/statusDevice"); // Đọc trạng thái bơm từ firebase

  
  int state = digitalRead(BUTTON) ;
  Serial.println("state = "+String(state));
  statusPump = Firebase.getInt("/list-devices-control/aKygDmC8JXNOtdR6iHSuVerHdvX2/devices-control/Pump/statusDevice"); // Đọc trạng thái bơm từ firebase
//  if(statusControlPump == 1) {
//    digitalWrite(ROLE, HIGH);
//  }else {
//    digitalWrite(ROLE, LOW);
//  }
 
  if( state == HIGH) {
//    if(statusControlPump == 0) {
//      Serial.println("Not Pressed");
//      digitalWrite(ROLE, LOW);
//      lcd.print("C:OFF");
//    }else {
//       Serial.println("Not Pressed");
//       digitalWrite(ROLE, HIGH);
//       lcd.print("C:ON");
//    }
     if(statusOn == 0) {
        Serial.println("Not Pressed");
//        digitalWrite(ROLE, LOW);
//        lcd.print("C:OFF");
      }else {
         Serial.println("Not Pressed");
//         digitalWrite(ROLE, HIGH);
//         lcd.print("C:ON");
      }
  }else {
      if(statusOn == 0) {
        Serial.println("Pressed");
//        digitalWrite(ROLE, HIGH);
//        lcd.print("C:ON");
        Firebase.setInt("/list-devices-control/aKygDmC8JXNOtdR6iHSuVerHdvX2/devices-control/Pump/statusDevice", 1);
      }else {
        Serial.println("Pressed");
//        digitalWrite(ROLE, LOW);
//        lcd.print("C:OFF");
        Firebase.setInt("/list-devices-control/aKygDmC8JXNOtdR6iHSuVerHdvX2/devices-control/Pump/statusDevice", 0);
      }
  }
   lcd.setCursor(10, 1);
   statusPump = Firebase.getInt("/list-devices-control/aKygDmC8JXNOtdR6iHSuVerHdvX2/devices-control/Pump/statusDevice"); // Đọc trạng thái bơm từ firebase
   if(statusPump == 1) {
    digitalWrite(ROLE, HIGH);
     lcd.print("C:ON");
  }else {
    digitalWrite(ROLE, LOW);
     lcd.print("C:OFF");
  }
  delay(20);
  delay(5000);
  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("T:"+String(t));
  lcd.print((char)223);
  lcd.print("C");
  lcd.print(" ");
  lcd.print("M:"+String(percent)+"%");
  lcd.setCursor(0, 1);
  lcd.print("H:"+String(h) + "%");
  if(percent > 30 ) {
      if(statusPump == 0){
        Firebase.setInt("/list-devices-control/aKygDmC8JXNOtdR6iHSuVerHdvX2/devices-control/Pump/statusDevice", 1);
        statusOn = 1;
         digitalWrite(ROLE, HIGH);
         lcd.print("C:ON");
      }
   }else {
      if(statusPump == 1) {
         statusOn = 0 ;
         Firebase.setInt("/list-devices-control/aKygDmC8JXNOtdR6iHSuVerHdvX2/devices-control/Pump/statusDevice", 0);
          digitalWrite(ROLE, LOW);
          lcd.print("C:OFF");
      }
    }
   delay(1000);
}
