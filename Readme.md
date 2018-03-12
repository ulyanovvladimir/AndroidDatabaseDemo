# Макеты
Добавьте макет для разметки главного экрана, содержащий список, компонент ListView

fragment_contacts.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:background="#ffffff"
    >

    <ListView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:id="@+id/lvcontacts">

    </ListView>
</LinearLayout>
```

contacts_item.xml
```xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="fill_parent"
    android:layout_height="?android:attr/listPreferredItemHeight"

    android:padding="6dip">

    <ImageView
        android:id="@+id/icon"

        android:layout_width="40dp"
        android:layout_height="fill_parent"
        android:layout_marginRight="6dip"

        android:src="@drawable/icon_how_to_contact"
        android:layout_weight="0.05" />

    <LinearLayout
        android:orientation="vertical"

        android:layout_width="0dip"
        android:layout_weight="1"
        android:layout_height="fill_parent">

        <TextView
            android:id="@+id/item_name"
            android:layout_width="fill_parent"
            android:layout_height="0dip"

            android:layout_weight="1"
            android:gravity="center_vertical"
            android:text="My Application"
            android:textSize="18sp"
            android:textStyle="bold" />

        <TextView
            android:layout_width="fill_parent"
            android:layout_height="0dip"
            android:layout_weight="1"

            android:ellipsize="marquee"
            android:text="Simple application that shows how to use RelativeLayout"
            android:id="@+id/item_phone"
            android:maxLines="1" />

        <TextView
            android:id="@+id/item_id"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="center_vertical"
            android:text="id"
            android:visibility="gone">
        </TextView>

    </LinearLayout>

</LinearLayout>

```


# Файл базы данных
Создайте файл базы данных SQLite, содержащую таблицу
Contacts со следующими полями:
- id
- name
- phone



