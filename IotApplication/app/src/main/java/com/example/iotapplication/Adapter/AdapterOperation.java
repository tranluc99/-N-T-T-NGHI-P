package com.example.iotapplication.Adapter;

//public class AdapterOperation extends RecyclerView.Adapter<AdapterOperation.OperationViewHolder> {
//
//    List<Operation> mList ;
//    Context mContext;
//
//    public AdapterOperation(List<Operation> mList, Context mContext) {
//        this.mList = mList;
//        this.mContext = mContext;
//    }
//
//    @NonNull
//    @Override
//    public OperationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(mContext).inflate(R.layout.item_operation,parent,false);
//        return new OperationViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull OperationViewHolder holder, int position) {
//        Operation operation = mList.get(position);
//        if(operation == null ) return ;
//        if(operation.getStatus() == 1)
//        {
//            holder.txtStatus.setText("Trạng Thái :  Bật");
//        }else {
//            holder.txtStatus.setText("Trạng Thái :  Tắt");
//        }
//        holder.txtTime.setText(operation.getTime());
//    }
//
//    @Override
//    public int getItemCount() {
//        return mList.size();
//    }
//
//    public class OperationViewHolder extends RecyclerView.ViewHolder{
//
//        public TextView txtStatus , txtTime ;
//        public OperationViewHolder(@NonNull View itemView) {
//            super(itemView);
//
//            txtStatus = itemView.findViewById(R.id.txt_status);
//            txtTime = itemView.findViewById(R.id.txt_time);
//        }
//    }
//}
