package com.liubowang.photoretouch.Normal;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.liubowang.photoretouch.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heshaobo on 2017/11/17.
 */

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ColorViewHolder> {


    private static final String TAG = ColorAdapter.class.getSimpleName();
    private List<ColorModel> dataSourceList = new ArrayList<>();
    private ColorItemClickListener itemClickListener;
    private int currentSelectedPosition;
    public ColorAdapter(){
        super();
        init();
    }

    public void setItemClickListener(ColorItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    private void init(){
        dataSourceList = getDataSource();
        dataSourceList.get(0).isSeleced = true;
        currentSelectedPosition = 0;
    }

    @Override
    public ColorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.view_color_item,parent,false);
        ColorViewHolder holder = new ColorViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ColorViewHolder holder, int position) {
        holder.bindItem(dataSourceList.get(position),position);
    }

    @Override
    public void onBindViewHolder(ColorViewHolder holder, int position, List payloads) {
        if (payloads.isEmpty()){
            onBindViewHolder(holder, position);
        }else {
            ColorModel oldSelecedModel = dataSourceList.get(position);
            holder.colorItem.setModel(oldSelecedModel);
        }

    }

    @Override
    public int getItemCount() {
        return dataSourceList.size();
    }

    class ColorViewHolder extends RecyclerView.ViewHolder {

        public ColorItemView colorItem;
        public ColorModel colorModel;
        public ColorViewHolder(View itemView) {
            super(itemView);
            colorItem = itemView.findViewById(R.id.ci_color_item_civ);
            colorItem.setOnClickListener(listener);
        }


        public void bindItem(ColorModel colorModel,int position){
            this.colorModel = colorModel;
            colorItem.setModel(colorModel);
        }

        private View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (colorModel.isSeleced) return;
                colorModel.isSeleced = true;
                colorItem.setModel(colorModel);
                ColorModel oldSelecedModel = dataSourceList.get(currentSelectedPosition);
                oldSelecedModel.isSeleced = false;
                notifyItemChanged(currentSelectedPosition,oldSelecedModel);
                currentSelectedPosition = getPosition();
                if (itemClickListener != null){
                    itemClickListener.itemClick(colorModel.color,colorModel.isSeleced);
                }
            }
        };
    }

    interface ColorItemClickListener {
        void itemClick(int color,boolean selected);
    }

    private  List<ColorModel> getDataSource(){
        List<String> colorStringList = getColorList();
        List<ColorModel> models = new ArrayList<>();
        for (String colorStr: colorStringList) {
            ColorModel model = new ColorModel(colorStr);
            Log.d(TAG,colorStr);
            models.add(model);
        }
        return models;
    }




    private  List<String> getColorList (){
        ArrayList<String> list = new ArrayList<>();
        list.add("#FFFFFF");	 	list.add("#FFFFF0");        list.add("#FFFFE0");        list.add("#FFFF00");
        list.add("#FFFAFA"); 	 	list.add("#FFFAF0");        list.add("#FFFACD");        list.add("#FFF8DC");
        list.add("#FFF68F"); 	 	list.add("#FFF5EE");        list.add("#FFF0F5");        list.add("#FFEFDB");
        list.add("#FFEFD5"); 	 	list.add("#FFEC8B");        list.add("#FFEBCD");        list.add("#FFE7BA");
        list.add("#FFE4E1"); 	 	list.add("#FFE4C4");        list.add("#FFE4B5");        list.add("#FFE1FF");
        list.add("#FFDEAD"); 	 	list.add("#FFDAB9");        list.add("#FFD700");        list.add("#FFD39B");
        list.add("#FFC1C1"); 	 	list.add("#FFC125");        list.add("#FFC0CB");        list.add("#FFBBFF");
        list.add("#FFB90F"); 	 	list.add("#FFB6C1");        list.add("#FFB5C5");        list.add("#FFAEB9");
        list.add("#FFA54F"); 	 	list.add("#FFA500");        list.add("#FFA07A");        list.add("#FF8C69");
        list.add("#FF8C00"); 	 	list.add("#FF83FA");        list.add("#FF82AB");        list.add("#FF8247");
        list.add("#FF7F50"); 	 	list.add("#FF7F24");        list.add("#FF7F00");        list.add("#FF7256");
        list.add("#FF6EB4"); 	 	list.add("#FF6A6A");        list.add("#FF69B4");        list.add("#FF6347");
        list.add("#FF4500"); 	 	list.add("#FF4040");        list.add("#FF3E96");        list.add("#FF34B3");
        list.add("#FF3030");	 	list.add("#FF1493");        list.add("#FF00FF");        list.add("#FF0000");
        list.add("#FDF5E6");	 	list.add("#FCFCFC");        list.add("#FAFAFA");        list.add("#FAFAD2");
        list.add("#FAF0E6");	 	list.add("#FAEBD7");        list.add("#FA8072");        list.add("#F8F8FF");
        list.add("#F7F7F7"); 	 	list.add("#F5FFFA");        list.add("#F5F5F5");        list.add("#F5F5DC");
        list.add("#F5DEB3");	 	list.add("#F4F4F4");        list.add("#F4A460");        list.add("#F2F2F2");
        list.add("#F0FFFF");	 	list.add("#F0FFF0");        list.add("#F0F8FF");        list.add("#F0F0F0");
        list.add("#F0E68C"); 	 	list.add("#F08080");        list.add("#EEEEE0");        list.add("#EEEED1");
        list.add("#EEEE00"); 	 	list.add("#EEE9E9");        list.add("#EEE9BF");        list.add("#EEE8CD");
        list.add("#EEE8AA"); 	 	list.add("#EEE685");        list.add("#EEE5DE");        list.add("#EEE0E5");
        list.add("#EEDFCC"); 	 	list.add("#EEDC82");        list.add("#EED8AE");        list.add("#EED5D2");
        list.add("#EED5B7");	 	list.add("#EED2EE");        list.add("#EECFA1");        list.add("#EECBAD");
        list.add("#EEC900");	    list.add("#EEC591");        list.add("#EEB4B4");        list.add("#EEB422");
        list.add("#EEAEEE"); 	 	list.add("#EEAD0E");        list.add("#EEA9B8");        list.add("#EEA2AD");
        list.add("#EE9A49");	    list.add("#EE9A00");        list.add("#EE9572");        list.add("#EE82EE");
        list.add("#EE8262");	 	list.add("#EE7AE9");        list.add("#EE799F");        list.add("#EE7942");
        list.add("#EE7621"); 	 	list.add("#EE7600");        list.add("#EE6AA7");        list.add("#EE6A50");
        list.add("#EE6363");	 	list.add("#EE5C42");	 	list.add("#EE4000"); 	    list.add("#EE3B3B");
        list.add("#EE3A8C");	 	list.add("#EE30A7");	 	list.add("#EE2C2C");	 	list.add("#EE1289");
        list.add("#EE00EE");	 	list.add("#EE0000");	 	list.add("#EDEDED");	 	list.add("#EBEBEB");
        list.add("#EAEAEA");	 	list.add("#E9967A");	 	list.add("#E8E8E8");	 	list.add("#E6E6FA");
        list.add("#E5E5E5");	 	list.add("#E3E3E3");	 	list.add("#E0FFFF");	 	list.add("#E0EEEE");
        list.add("#E0EEE0");	 	list.add("#E0E0E0");	 	list.add("#E066FF");	 	list.add("#DEDEDE");
        list.add("#DEB887");	 	list.add("#DDA0DD");	 	list.add("#DCDCDC");	 	list.add("#DC143C");
        list.add("#DBDBDB");	 	list.add("#DB7093");	 	list.add("#DAA520");	 	list.add("#DA70D6");
        list.add("#D9D9D9");	 	list.add("#D8BFD8");	 	list.add("#D6D6D6");	 	list.add("#D4D4D4");
        list.add("#D3D3D3");	 	list.add("#D2B48C");	 	list.add("#D2691E");	 	list.add("#D1EEEE");
        list.add("#D1D1D1");	 	list.add("#D15FEE");	 	list.add("#D02090");	 	list.add("#CFCFCF");
        list.add("#CDCDC1");	 	list.add("#CDCDB4");	 	list.add("#CDCD00");	 	list.add("#CDC9C9");
        list.add("#CDC9A5");	 	list.add("#CDC8B1");	 	list.add("#CDC673");	 	list.add("#CDC5BF");
        list.add("#CDC1C5");	 	list.add("#CDC0B0");	 	list.add("#CDBE70");	 	list.add("#CDBA96");
        list.add("#CDB7B5");	 	list.add("#CDB79E");	 	list.add("#CDB5CD");	 	list.add("#CDB38B");
        list.add("#CDAF95");	 	list.add("#CDAD00");	 	list.add("#CDAA7D");	 	list.add("#CD9B9B");
        list.add("#CD9B1D");	 	list.add("#CD96CD");	 	list.add("#CD950C");	 	list.add("#CD919E");
        list.add("#CD8C95");	 	list.add("#CD853F");	 	list.add("#CD8500");	 	list.add("#CD8162");
        list.add("#CD7054");	 	list.add("#CD69C9");	 	list.add("#CD6889");	 	list.add("#CD6839");
        list.add("#CD661D");	 	list.add("#CD6600");	 	list.add("#CD6090");	 	list.add("#CD5C5C");
        list.add("#CD5B45");	 	list.add("#CD5555");	 	list.add("#CD4F39");	 	list.add("#CD3700");
        list.add("#CD3333");	 	list.add("#CD3278");	 	list.add("#CD2990");	 	list.add("#CD2626");
        list.add("#CD1076");	 	list.add("#CD00CD");	 	list.add("#CD0000");	 	list.add("#CCCCCC");
        list.add("#CAFF70");	 	list.add("#CAE1FF");	 	list.add("#C9C9C9");	 	list.add("#C7C7C7");
        list.add("#C71585");	 	list.add("#C6E2FF");	 	list.add("#C67171");	 	list.add("#C5C1AA");
        list.add("#C4C4C4");	 	list.add("#C2C2C2");	 	list.add("#C1FFC1");	 	list.add("#C1CDCD");
        list.add("#C1CDC1");	 	list.add("#C1C1C1");	 	list.add("#C0FF3E");	 	list.add("#BFEFFF");
        list.add("#BFBFBF");	 	list.add("#BF3EFF");	 	list.add("#BEBEBE");	 	list.add("#BDBDBD");
        list.add("#BDB76B");	 	list.add("#BCEE68");	 	list.add("#BCD2EE");	 	list.add("#BC8F8F");
        list.add("#BBFFFF");	 	list.add("#BABABA");	 	list.add("#BA55D3");	 	list.add("#B9D3EE");
        list.add("#B8B8B8");	 	list.add("#B8860B");	 	list.add("#B7B7B7");	 	list.add("#B5B5B5");
        list.add("#B4EEB4");	 	list.add("#B4CDCD");	 	list.add("#B452CD");	 	list.add("#B3EE3A");
        list.add("#B0E2FF");	 	list.add("#B0E0E6");	 	list.add("#B0C4DE");	 	list.add("#B0B0B0");
        list.add("#B03060");	 	list.add("#AEEEEE");	 	list.add("#ADFF2F");	 	list.add("#ADD8E6");
        list.add("#ADADAD");	 	list.add("#ABABAB");	 	list.add("#AB82FF");	 	list.add("#AAAAAA");
        list.add("#A9A9A9");	 	list.add("#A8A8A8");	 	list.add("#A6A6A6");	 	list.add("#A52A2A");
        list.add("#A4D3EE");	 	list.add("#A3A3A3");	 	list.add("#A2CD5A");	 	list.add("#A2B5CD");
        list.add("#A1A1A1");	 	list.add("#A0522D");	 	list.add("#A020F0");	 	list.add("#9FB6CD");
        list.add("#9F79EE");	 	list.add("#9E9E9E");	 	list.add("#9C9C9C");	 	list.add("#9BCD9B");
        list.add("#9B30FF");	 	list.add("#9AFF9A");	 	list.add("#9ACD32");	 	list.add("#9AC0CD");
        list.add("#9A32CD");	 	list.add("#999999");	 	list.add("#9932CC");	 	list.add("#98FB98");
        list.add("#98F5FF");	 	list.add("#97FFFF");	 	list.add("#96CDCD");	 	list.add("#969696");
        list.add("#949494");	 	list.add("#9400D3");	 	list.add("#9370DB");	 	list.add("#919191");
        list.add("#912CEE");	 	list.add("#90EE90");	 	list.add("#8FBC8F");	 	list.add("#8F8F8F");
        list.add("#8EE5EE");	 	list.add("#8E8E8E");	 	list.add("#8E8E38");	 	list.add("#8E388E");
        list.add("#8DEEEE");	 	list.add("#8DB6CD");	 	list.add("#8C8C8C");	 	list.add("#8B8B83");
        list.add("#8B8B7A");	 	list.add("#8B8B00");	 	list.add("#8B8989");	 	list.add("#8B8970");
        list.add("#8B8878");	 	list.add("#8B8682");	 	list.add("#8B864E");	 	list.add("#8B8386");
        list.add("#8B8378");	 	list.add("#8B814C");	 	list.add("#8B7E66");	 	list.add("#8B7D7B");
        list.add("#8B8378");	 	list.add("#8B814C");	 	list.add("#8B7E66");	 	list.add("#8B7D7B");
        list.add("#8B8378");	 	list.add("#8B814C");	 	list.add("#8B7E66");	 	list.add("#8B7D7B");
        list.add("#8B7D6B");	 	list.add("#8B7B8B");	 	list.add("#8B795E");	 	list.add("#8B7765");
        list.add("#8B7500");	 	list.add("#8B7355");	 	list.add("#8B6969");	 	list.add("#8B6914");
        list.add("#8B668B");	 	list.add("#8B6508");	 	list.add("#8B636C");	 	list.add("#8B5F65");
        list.add("#8B5A2B");	 	list.add("#8B5A00");	 	list.add("#8B5742");	 	list.add("#8B4C39");
        list.add("#8B4789");	 	list.add("#8B475D");	 	list.add("#8B4726");	 	list.add("#8B4513");
        list.add("#8B4500");	 	list.add("#8B3E2F");	 	list.add("#8B3A62");	 	list.add("#8B3A3A");
        list.add("#8B3626");	 	list.add("#8B2500");	 	list.add("#8B2323");	 	list.add("#8B2252");
        list.add("#8B1C62");	 	list.add("#8B1A1A");	 	list.add("#8B0A50");	 	list.add("#8B008B");
        list.add("#8B0000");	 	list.add("#8A8A8A");	 	list.add("#8A2BE2");	 	list.add("#8968CD");
        list.add("#87CEFF");	 	list.add("#87CEFA");	 	list.add("#87CEEB");	 	list.add("#878787");
        list.add("#858585");	 	list.add("#848484");	 	list.add("#8470FF");	 	list.add("#838B8B");
        list.add("#838B83");	 	list.add("#836FFF");	 	list.add("#828282");	 	list.add("#7FFFD4");
        list.add("#7FFF00");	 	list.add("#7F7F7F");	 	list.add("#7EC0EE");	 	list.add("#7D9EC0");
        list.add("#7D7D7D");	 	list.add("#7D26CD");	 	list.add("#7CFC00");	 	list.add("#7CCD7C");
        list.add("#7B68EE");	 	list.add("#7AC5CD");	 	list.add("#7A8B8B");	 	list.add("#7A7A7A");
        list.add("#7A67EE");	 	list.add("#7A378B");	 	list.add("#79CDCD");	 	list.add("#787878");
        list.add("#778899");	 	list.add("#76EEC6");	 	list.add("#76EE00");	 	list.add("#757575");
        list.add("#737373");	 	list.add("#71C671");	 	list.add("#7171C6");	 	list.add("#708090");
        list.add("#707070");	 	list.add("#6E8B3D");	 	list.add("#6E7B8B");	 	list.add("#6E6E6E");
        list.add("#6CA6CD");	 	list.add("#6C7B8B");	 	list.add("#6B8E23");	 	list.add("#6B6B6B");
        list.add("#6A5ACD");	 	list.add("#698B69");	 	list.add("#698B22");	 	list.add("#696969");
        list.add("#6959CD");	 	list.add("#68838B");	 	list.add("#68228B");	 	list.add("#66CDAA");
        list.add("#66CD00");	 	list.add("#668B8B");	 	list.add("#666666");	 	list.add("#6495ED");
        list.add("#63B8FF");	 	list.add("#636363");	 	list.add("#616161");	 	list.add("#607B8B");
        list.add("#5F9EA0");	 	list.add("#5E5E5E");	 	list.add("#5D478B");	 	list.add("#5CACEE");
        list.add("#5C5C5C");	 	list.add("#5B5B5B");	 	list.add("#595959");	 	list.add("#575757");
        list.add("#556B2F");	 	list.add("#555555");	 	list.add("#551A8B");	 	list.add("#54FF9F");
        list.add("#548B54");	 	list.add("#545454");	 	list.add("#53868B");	 	list.add("#528B8B");
        list.add("#525252");	 	list.add("#515151");	 	list.add("#4F94CD");	 	list.add("#4F4F4F");
        list.add("#4EEE94");	 	list.add("#4D4D4D");	 	list.add("#4B0082");	 	list.add("#4A708B");
        list.add("#4A4A4A");	 	list.add("#48D1CC");	 	list.add("#4876FF");	 	list.add("#483D8B");
        list.add("#474747");	 	list.add("#473C8B");	 	list.add("#4682B4");	 	list.add("#458B74");
        list.add("#458B00");	 	list.add("#454545");	 	list.add("#43CD80");	 	list.add("#436EEE");
        list.add("#424242");	 	list.add("#4169E1");	 	list.add("#40E0D0");	 	list.add("#404040");
        list.add("#3D3D3D");	 	list.add("#3CB371");	 	list.add("#3B3B3B");	 	list.add("#3A5FCD");
        list.add("#388E8E");	 	list.add("#383838");	 	list.add("#36648B");	 	list.add("#363636");
        list.add("#333333");	 	list.add("#32CD32");	 	list.add("#303030");	 	list.add("#2F4F4F");
        list.add("#2E8B57");	 	list.add("#2E2E2E");	 	list.add("#2B2B2B");	 	list.add("#292929");
        list.add("#282828");	 	list.add("#27408B");	 	list.add("#262626");	 	list.add("#242424");
        list.add("#228B22");	 	list.add("#218868");	 	list.add("#212121");	 	list.add("#20B2AA");
        list.add("#1F1F1F");	 	list.add("#1E90FF");	 	list.add("#1E1E1E");	 	list.add("#1C86EE");
        list.add("#1C1C1C");	 	list.add("#1A1A1A");	 	list.add("#191970");	 	list.add("#1874CD");
        list.add("#171717");	 	list.add("#141414");	 	list.add("#121212");	 	list.add("#104E8B");
        list.add("#0F0F0F");	 	list.add("#0D0D0D");	 	list.add("#0A0A0A");	 	list.add("#080808");
        list.add("#00FF00");	 	list.add("#00FA9A");	 	list.add("#00F5FF");	 	list.add("#00EEEE");
        list.add("#00EE76");	 	list.add("#00EE00");	 	list.add("#00E5EE");	 	list.add("#00CED1");
        list.add("#00CDCD");	 	list.add("#00CD66");	 	list.add("#00CD00");	 	list.add("#00C5CD");
        list.add("#00BFFF");	 	list.add("#00B2EE");	 	list.add("#009ACD");	 	list.add("#008B8B");
        list.add("#008B45");	 	list.add("#008B00");	 	list.add("#00868B");	 	list.add("#00688B");
        list.add("#006400");	 	list.add("#0000FF");	 	list.add("#0000EE");	 	list.add("#0000CD");
        list.add("#0000AA");	 	list.add("#00008B");	 	list.add("#000080");	 	list.add("#000000");
        return list;
    }

}
