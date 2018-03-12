# Список на базе CursorAdapter и звонки на номера абонентов

Данные пошаговые инструкции помогут вам подключить базу данных к Android приложению,
использовать Cursor для выборки данных, SimpleCursorAdapter для построения списка с помощью компоненты
ListView. Начинаем с шаблонного приложения с пустым экраном активности.

## Макеты
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

Макет contacts_item.xml будет размечать отдельый элемент списка.

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
Не забудьте заменить макет по умолчанию на макет fragment_contains.xml:

```java 
setContentView(R.layout.fragment_contacts);
```

## Файл базы данных
Создайте файл базы данных SQLite, содержащую таблицу
`person` со следующими полями:
- id
- fullname
- phone

## assets
Добавьте папку assets

Для этого в Android Studio можно воспользоваться меню

File -> New -> Folder -> Assets Folder

Скопируйте в данную папку ваш файл с базой данных. Если имя файла базы даннных отличается от 
helper.db, то исправить соответствующие настройки в классе DatabaseHelper.

Имя файла базы хранится в константе 'DB_NAME'


## Подключение к БД
  
 В метод активности onCreate добавьте следующий код:
```java
    mDBHelper = new DatabaseHelper(this);
 
     // обновление базы данных
     try {
         mDBHelper.updateDataBase();
     } catch (IOException e) {
         throw new RuntimeException("Невозможно загрузить базу данных", e);
     }

     mDb = mDBHelper.getWritableDatabase();

 
```
Добавьте недостающие поля класса mDBHelper и mDb соответствующего типа.

Этот код произведет установку/обновление базы данных из файла папки assets, затем 
подключится к БД в режиме записи.

Для того, чтобы гарантировать отключение от БД и освобождение ресурсов, рекомендуется делать это в
парном методе onDestroy() класса Activity. Добавьте в MainActivity следующий метод:

```java
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Закрываем соединение с базой данных
        mDBHelper.close();
    }
```

## CursorAdapter  и ListView
Компонента ListView представляет собой класс, наследуемые от AdapterView, а значит, работа с данными
происходит посредством адаптера. Для упрощения создания адаптеров для работы с базами данных в 
Андроид есть класс CursorAdapter...

Адаптер свяжет компоненту и  выборку из базы данных. Выборка из таблцы производится посредством
специального объекта, курсора. Количество элементов списка будет таким же, сколько строк в таблице.
Для каждой строки таблицы будет произведено отображение в элемент компонента ListView. При этом 
значение ячейки в каждом столбце будет ассоциировано с определенной компонентой макета/лэйаута
contacts.xml посредством заголовков.

Начнем с того, что определим поля макета для отдельной плашки компоненты списка:
 
```java
        //определяем поля макета, лэйаута, в которые будут выводиться значения столбцов
        int[] fields = new int[]{R.id.item_id, R.id.item_name, R.id.item_phone};
```
Это массив идентификаторов ресурсов в макете contacts_item.xml, который будем использовать для
отдельной плашки ListView.

Затем создадим массив заголовков столбцов таблицы базы данных.
```java

        // заголовки столбцов результирующей таблицы-выборки из базы данных
        String[] headers = new String[]{"_id", "fullname", "phone"};
        
```
Мы будем делать запрос `SELECT id, fullname, phone FROM person` что создаст на выходе таблицу из трех
столбцов. Имена/заголовки столбцов результирующей таблицы выборки и будут в массиве строк.
 
Далее выполним SQL запрос, получив объект курсора для работы с выборкой 
```java

        // сделаем выборку данных из базы данных, указав необходимые столбцы и таблицу
        // последний параметр null означает, что мы не делаем фильтрацию, выбирам все строки таблицы БД.
        Cursor c = mDb.rawQuery("SELECT id AS _id, fullname, phone FROM person", null);
        
```
Этот объект подготовит данные, выберет их из базы данных и поставит "курсор" в начало первой строки,
готовый считывать данные в цикле.

Но руками нам ничего считывать не придется, т.к. именно эту работу и реализует SimpleCursorAdapter.
Нам нужно лишь сопоставить каждую строку таблицы плашке интерфейса, каждый столбецц -- соответствующей
компоненте. 

У нас все готово для создания адаптера.

```java

        // на базе курсора создаем адаптер, используя все заготовленные параметры и данные.
        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this, R.layout.contacts_item, c, headers, fields, SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
```
ДАнный конструктор класса SimpleCursorAdapter принимает Context, затем данные в виде курсора,
затем заголовки столбцов и поля интерфейса, напоследок идут флаги, которые пока можно игнорировать.

Адаптер готов предоставить всю полноту информации для отрисовки компоненте. Найдем ее и передадим
адаптер стандартным образом.
```java
        //Находим компоненту списка
        ListView lvContacts = (ListView) findViewById(R.id.lvcontacts);

        //Применяем к ней адаптер
        lvContacts.setAdapter(cursorAdapter);
    }
```

В итоге у вас должен получиться следующий код в MainActivity:

```java
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_contacts);

        mDBHelper = new DatabaseHelper(this);

        //
        try {
            mDBHelper.updateDataBase();
        } catch (IOException e) {
            throw new RuntimeException("Невозможно загрузить базу данных", e);
        }

        mDb = mDBHelper.getWritableDatabase();

        //определяем поля макета, лэйаута, в которые будут выводиться значения столбцов
        int[] fields = new int[]{R.id.item_id, R.id.item_name, R.id.item_phone};

        // заголовки столбцов результирующей таблицы-выборки из базы данных
        String[] headers = new String[]{"_id", "fullname", "phone"};
        // сделаем выборку данных из базы данных, указав необходимые столбцы и таблицу
        // последний параметр null означает, что мы не делаем фильтрацию, выбирам все строки таблицы БД.
        Cursor c = mDb.rawQuery("SELECT id AS _id, fullname, phone FROM person", null);
        // на базе курсора создаем адаптер, используя все заготовленные параметры и данные.
        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this, R.layout.contacts_item, c, headers, fields, SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        //Находим компоненту списка
        ListView lvContacts = (ListView) findViewById(R.id.lvcontacts);

        //Применяем к ней адаптер
        lvContacts.setAdapter(cursorAdapter);
    }
```


## Звонилка
В качестве вишенки на торте, реализуем следующую функциональность: при нажатии на элемент списка
 осуществляется звонок на указанный номер телефона.

Для обработки нажатий на элементы списка, добавим обработку листинера OnItemClickListener. 
В методе onCreate допишем:

```java
//добавляем обработку нажатия на элемент из списка контактов
lvContacts.setOnItemClickListener(this);
```

Для реализации потребуется объявить класс MainActivity реализующим соответствующий интерфейс, но сперва
 добавим константу для запроса пользователя разрешения на доступ нашего приложения к осуществлению звонков
```java 
private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 0x000000;
```

затем реализуем интерфейс выбора элемента списка
```java
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String phoneNumber = ((TextView)view.findViewById(R.id.item_phone)).getText().toString();
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CALL_PHONE},
                    MY_PERMISSIONS_REQUEST_CALL_PHONE);

            // MY_PERMISSIONS_REQUEST_CALL_PHONE это определенная константа
            // callback-метод onRequestPermissionResult обрабатывает результаты выбора ползователя
        } else {
            //Уже есть разрешение
            try {
                startActivity(intent); //звоним
            } catch(SecurityException e) {
                e.printStackTrace();
            }
        }
        //startActivity(intent);
    }
```
Это callback-метод, который опционально может реагировать на выбор пользователя в плане разрешений.
```java
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                // Если запрос отменен, то результирующие массивы пустые
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // разрешение получено, можно звонить
                } else {
                    // разрешение не получено
                    // отключаем соответствующую функциональность
                }
            }
        }
    }

```

В процессе нам потребуется добавить запрос на наше разрешение в манифест, 
```xml
<uses-permission android:name="android.permission.CALL_PHONE" />
```
но Android Studio может сделать это за нас посредством ALT+ENTER на CALL_PHONE



## GIT
В завершении для того, чтобы зафиксировать изменения введите в консоли команду

```bash
git add .
```
Она добавит все файлы, которые вы создавали или изменяли к сохранению.

Далее сохраним изменения командой
```bash
git commit -m "БД + CursorAdapter"
```

Отправим обновления на сервер:
```bash
git push origin master
```