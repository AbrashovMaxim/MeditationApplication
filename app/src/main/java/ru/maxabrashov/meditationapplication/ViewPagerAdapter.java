package ru.maxabrashov.meditationapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import ru.maxabrashov.meditationapplication.R.layout;

import java.util.List;

import kotlin.jvm.internal.Intrinsics;

public final class ViewPagerAdapter extends RecyclerView.Adapter {
    @NotNull
    private final List images; // Поле со списком изображений

    // Инициализируем ViewPager
    public ViewPagerAdapter(@NotNull List images) {
        Intrinsics.checkNotNullParameter(images, "images");
        this.images = images;
    }

    // Создаем Холдер
    @NotNull
    public ViewPagerViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        Intrinsics.checkNotNullParameter(parent, "parent");
        View view = LayoutInflater.from(parent.getContext()).inflate(layout.item_view_pager, parent, false);
        Intrinsics.checkNotNullExpressionValue(view, "view");
        return new ViewPagerViewHolder(view);
    }

    // Устанавливаем в ViewPager все необходимые компоненты из шаблона item_view_pager
    public void onBindViewHolder(@NotNull ViewPagerViewHolder holder, int position) {
        Intrinsics.checkNotNullParameter(holder, "holder");
        int curImage = ((Number)this.images.get(position)).intValue();
        holder.getItemImage().setImageResource(curImage);
    }

    public void onBindViewHolder(RecyclerView.ViewHolder var1, int var2) {
        this.onBindViewHolder((ViewPagerViewHolder)var1, var2);
    }

    public int getItemCount() { // Получаем количество изображений
        return this.images.size();
    }

    public final class ViewPagerViewHolder extends RecyclerView.ViewHolder {
        @NotNull
        private final ImageView itemImage; // Поле виджета ItemView элемента

        public ViewPagerViewHolder(@NotNull View itemView) { // Инициализируем элемент
            super(itemView);
            View var10001 = itemView.findViewById(R.id.ivImage); // Получаем из шаблона item_view_pager - ImageView
            this.itemImage = (ImageView)var10001;
        }

        @NotNull
        public final ImageView getItemImage() { // Получаем ImageView
            return this.itemImage;
        }
    }
}