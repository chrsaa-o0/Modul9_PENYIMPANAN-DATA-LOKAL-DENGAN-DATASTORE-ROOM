Modul9_PenyimpananDataLokalDenganDataStoreRoom

Nama: Charisa Salsabila Larasati

NIM: 24515020111022

Deskripsi: Tugas praktikum Pengembangan Aplikasi Perangkat Bergerak (PAPB) mengenai Penyimpanan Data Lokal Menggunakan Preferences DataStore dan Room Database.

Proyek ini merupakan kelanjutan dari pengembangan aplikasi berbasis Clean Architecture yang mengintegrasikan sistem penyimpanan data persisten lokal secara penuh ke dalam aplikasi Android menggunakan Jetpack Compose. Fokus utama dari modul ini adalah menerapkan persistensi data lokal melalui dua pendekatan utama: Room Database untuk mengelola tabel relasional mahasiswa favorit secara reaktif, dan Preferences DataStore untuk menyimpan preferensi pengguna secara asinkron seperti riwayat pencarian terakhir (lastSearchQuery) serta pengaturan tambahan lainnya (sortAscending).

Dalam pengembangannya, arsitektur aplikasi dirancang secara ketat untuk memisahkan lapisan antarmuka (*UI Layer*) dari lapisan penyimpanan (*Data Layer*). Komponen Composable seperti HomeScreen, DetailScreen, dan FavoritesScreen murni hanya bertugas merender UI dan meneruskan aksi pengguna, sementara seluruh akses pembacaan maupun penulisan data ke database SQLite (melalui DAO Room) dan DataStore diisolasi secara rapi di dalam perantara Repository serta dikelola secara aman oleh ViewModel menggunakan Kotlin Coroutines Flow dan StateFlow. 

Selain itu, fitur utama seperti halaman FavoritesScreen berhasil diintegrasikan ke dalam Navigation Compose, memungkinkan pembaruan data secara realtime secara otomatis (*reactive UI*) tanpa *refresh* manual setiap kali status favorit diubah dari halaman detail. Seluruh struktur kode dan implementasi penyimpanan ini dirancang agar data tetap aman dan bertahan meskipun aplikasi ditutup total atau perangkat mengalami restart.
