package com.example.photoeditor.dashBoard.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.photoeditor.R
import java.util.ArrayList

class TextViewAdapter(private val onTextItemListener : OnTextItemListener): RecyclerView.Adapter<TextViewAdapter.ViewHolder>(){

    interface OnTextItemListener{
        fun onCropToolSelected(toolType: CropToolType)
    }

    private val mToolList: MutableList<ToolModel> = ArrayList()

    internal inner class ToolModel(
        val itemName : String,
        val itemIcon : Int,
        val mToolType: CropToolType
    )

    inner class ViewHolder(itemView : View): RecyclerView.ViewHolder(itemView){
        val imageView = itemView.findViewById<ImageView>(R.id.image_view_crop)
        val textView = itemView.findViewById<TextView>(R.id.text_view_crop)

        init {
            itemView.setOnClickListener { _: View ->
                onTextItemListener.onCropToolSelected(
                    mToolList[layoutPosition].mToolType
                )
            }
        }
    }

    init {
        mToolList.add(ToolModel("Size",R.drawable.font_size,CropToolType.FONT_SIZE))
        mToolList.add(ToolModel("Font",R.drawable.textfont,CropToolType.FONT_FAMILY))
        mToolList.add(ToolModel("Bold",R.drawable.fontbold,CropToolType.BOLD))
        mToolList.add(ToolModel("Italic",R.drawable.italic,CropToolType.ITALIC))
        mToolList.add(ToolModel("BG Colour",R.drawable.textbg,CropToolType.BG_COLOUR))
        mToolList.add(ToolModel("Colour",R.drawable.textcolor,CropToolType.TEXT_COLOR))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.crop_item_tools, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mToolList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mToolList[position]
        holder.textView.text = item.itemName
        holder.imageView.setImageResource(item.itemIcon)
    }

}

enum class CropToolType{
    FONT_SIZE, FONT_FAMILY, BOLD, ITALIC, BG_COLOUR, TEXT_COLOR
}