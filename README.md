Sebelum menjalankan testing pastikan sudah menginstall software boomgate dan menjalankannya

Setelah itu buka file boomgate-automation\src\main\java\com\boomgate\test\MainRunner.java dan ubah line 14 TamuCRUD.class menjadi Modul yang ingin dirubah sebagai contoh WargaCRUD.class

Untuk menjalankan kode, lakukan mvn compile exec:java "-Dexec.mainClass=com.boomgate.test.MainRunner"
