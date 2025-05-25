# Bukti Melakukan Profiling Aplikasi (Table Service)

Bagian ini mendokumentasikan langkah-langkah yang dilakukan untuk melakukan profiling pada `Table Service` menggunakan profiler bawaan IntelliJ. Tujuan dari profiling ini adalah untuk mengidentifikasi potensi bottleneck performa dan area yang memerlukan optimasi. Saya menggunakan profiling yang sudah tersedia di Intellij untuk kemudahan dan karena sudah terintegrasi langsung.

Berikut adalah langkah-langkah yang saya lakukan untuk melakukan profiling:

1.  **Memulai Sesi Profiling dari Toolbar:**
    Dengan proyek `table-service-rizzerve` sudah terbuka di IntelliJ, sesi profiling dimulai dengan memilih Run Configuration `TableServiceRizzerveApplication` yang sudah ada di toolbar atas. Kemudian, tombol "Profile" ditekan untuk memulai aplikasi dengan profiler terpasang. Opsi yang dipilih adalah "Profile 'TableServiceRizzerveApplication' with IntelliJ Profiler".
    
    ![Screenshot 2025-05-25 144602](https://github.com/user-attachments/assets/8aefc58a-b545-457f-8c0a-2463a5c60b89)

2.  **Menjalankan Skenario Pengujian:**
    Setelah aplikasi berjalan dengan profiler terpasang, skenario pengujian yang relevan dijalankan untuk memicu code path yang ingin dianalisis. Ini termasuk:
    *   Memanggil endpoint API untuk `createMeja`.
    *   Memanggil endpoint API untuk `findAllMeja`.
    *   Memanggil endpoint API untuk `findAllMejaForCustomer`.
    *   Memanggil endpoint API untuk `updateMeja`.
    *   Memanggil endpoint API untuk `deleteMeja`.
        Skenario ini dijalankan beberapa kali untuk memastikan data profiling yang representatif terkumpul.
        
    ![Screenshot 2025-05-25 144623](https://github.com/user-attachments/assets/2e96c81d-0233-4cd7-8cd2-34b3a9988cfe)


3.  **Menghentikan Sesi Profiling dan Menganalisis Hasil:**
    Setelah skenario pengujian selesai, sesi profiling dihentikan. IntelliJ kemudian menampilkan hasil profiling di *Profiler tool window*.
    
    ![Screenshot 2025-05-25 144725](https://github.com/user-attachments/assets/bd1bba9b-4a37-4f40-86fa-df16a93dc093)


    *   **Tampilan Awal Hasil Profiling (Contoh: CPU Time / Method List):**
        Hasil awal menunjukkan daftar method yang dieksekusi beserta metrik performanya seperti "CPU Time".

        ![Screenshot 2025-05-25 151656](https://github.com/user-attachments/assets/01542834-6c1e-49ca-b4ca-43efa488b4b6)


4.  **Analisis Mendalam pada Method yang Teridentifikasi:**
    Berdasarkan hasil awal, method-method yang memiliki "Execution time" tinggi diinvestigasi lebih lanjut.

    ![Screenshot 2025-05-25 144202](https://github.com/user-attachments/assets/089d380c-8838-4088-81f2-3cf1ddeef47e)

    Walaupun secara keseluruhan semua method sudah cukup efisien, bisa dilihat bahwa method `createMeja` disini memiliki execution time yang lebih tinggi dibandingkan dengan method-method yang lainnya, yaitu bisa hampir 2x-4x execution time dari method lain.

## Analisis Improvement Yang Perlu Dilakukan

Dari hasil profiling, bisa disimpulkan bahwa method `createMeja`  walaupun sudah cukup efisien bisa dioptimisasi lagi agar memiliki execution time yang lebih kecil dari sekarang. Salah satu improvement yang mungkin adalah men-handle duplication `Meja` di database saja dan tidak dalam kode langsung.

# Diagram A17 - Rizzerve
- Context Diagram
![image](https://github.com/user-attachments/assets/ffed3051-0812-4910-a090-19e41b8d4acb)

- Container Diagram
![Container Diagram](https://github.com/user-attachments/assets/43eea529-a781-4aa7-bc13-d88d1942598a)

- Deployment Diagram
![Deployment Diagram](https://github.com/user-attachments/assets/08b16e3f-c5ba-4752-aedc-7bc5eb85909e)

## Risk Storming - Future Architecture
- **Architecture Risk Matrix**
![messageImage_1747403599672](https://github.com/user-attachments/assets/9bfb378f-89e0-40c1-b11f-d9d36b3c38e9)

- **Future Context Diagram**
![rizzerve-a17-future-context-diagram](https://github.com/user-attachments/assets/5773394f-42ed-4b89-a84d-f74f403ee8e7)

- **Future Container Diagram**
![future-container-diagram](https://github.com/user-attachments/assets/27d2c8f4-e29a-4abf-ab9f-c43fd8eae4f3)

- **Future Deployment Diagram**
![future-deployment-diagram](https://github.com/user-attachments/assets/47d7de77-6b4e-4cb3-8d76-15d35ea57556)

## Consenssus
Setelah diskusi tim, disepakati bahwa dua risiko berikut memiliki skor tertinggi (6) dan menjadi prioritas mitigasi:
1. Single Database untuk Multi-Service
    * Kesepakatan Tim:
        * Semua partisipan setuju bahwa penggunaan satu database bersama oleh multi-service berpotensi menyebabkan:
            * Masalah migrasi schema (contoh: perubahan tabel oleh satu service mengganggu service lain).
            * Bottleneck performa saat beban tinggi.
    * Meskipun kemungkinan terjadi medium (karena saat ini masih dalam pengembangan), tetapi dampaknya high (dapat mengganggu seluruh operasi).

* Race Condition pada Booking Meja
    * Kesepakatan Tim:
        * Partisipan sepakat bahwa meja yang dipesan bersamaan oleh banyak customer (tanpa mekanisme locking) akan menyebabkan:
            * Double booking (satu meja terisi oleh lebih dari satu customer).
            * Pengalaman pengguna buruk (konflik saat checkout).
        * Kemungkinan terjadi high (karena akses concurrent), dampak medium (dapat diperbaiki dengan rollback, tetapi reputasi restoran terdampak).

## Mitigation
1. Mitigasi untuk Single Database Multi-Service
    * Solusi: Pisahkan database per microservice (isolasi database).
    * Kelebihan:
        - Migrasi Schema Lebih Aman: Perubahan schema di satu service tidak memengaruhi service lain.
        - Performa Lebih Baik: Menghindari bottleneck saat beban tinggi karena query terdistribusi.
        - Keamanan Meningkat: Jika satu database diretas, service lain tetap aman.
    * Kekurangan:
        - Biaya Operasional Naik: Butuh lebih banyak sumber daya untuk manage multiple database.
        - Kompleksitas Transaksi: Transaksi antar service memerlukan event-driven architecture (misal: Kafka).
        - Data Duplikasi: Beberapa data perlu direplikasi (contoh: info menu di Order Service dan Menu Service).
2. Mitigasi untuk Race Condition pada Booking Meja
    * Solusi: Implementasi optimistic locking dengan versioning atau distributed lock (Redis).
    * Kelebihan:
        - Mencegah Double Booking: Hanya satu customer yang bisa membooking meja dalam waktu bersamaan.
        - Responsif: Tidak mengunci seluruh sistem (hanya row/meja yang diproses).
        - Scalable: Cocok untuk arsitektur microservice.
    * Kekurangan:
        - Overhead Teknis: Butuh tambahan kode untuk handle conflict (contoh: retry mechanism).
        - Latency: Jika pakai Redis, tambahan jaringan untuk acquire/release lock.
        - Fallback Complex: Jika lock gagal, perlu mekanisme rollback (misal: notifikasi "meja sudah terisi").

## Justifikasi
Kami memilih teknik risk storming karena cara ini membuat semua anggota tim bicara tentang kemungkinan masalah sejak awal. Setiap orang memberi ide tentang apa yang bisa salah dan apa akibatnya. Setelah itu, kami menilai mana yang paling penting berdasarkan seberapa sering bisa terjadi dan seberapa besar gangguannya.

Untuk resiko “Satu Database untuk Banyak Layanan”, kami lihat bahwa memakai satu database memang cepat saat mulai, tapi bisa jadi titik gagal tunggal. Jika satu layanan mengubah skema, layanan lain bisa rusak. Saat trafik tinggi, database itu bisa kewalahan. Solusinya adalah pisahkan database untuk tiap layanan. Dengan begitu, perubahan skema di satu layanan tidak mengganggu yang lain, kapasitas bisa ditambah sesuai kebutuhan, dan sistem lebih mudah dipulihkan jika terjadi masalah.

Pada resiko “Bentrokan Saat Booking Meja”, banyak pengguna bisa pesan meja bersamaan dan menyebabkan double booking. Kami sepakat menggunakan optimistic locking atau kunci terdistribusi lewat Redis. Cara ini hanya mengunci data yang sedang diproses, bukan seluruh tabel. Jika ada dua permintaan bentrok, satu akan gagal dan dicoba ulang. Dengan begitu, meja tidak bisa dipakai dua kali dan pengalaman pengguna tetap mulus.

# Individual (Bertrand Gwynfory Iskandar - 2306152121)
- Component Diagram - Table Service
![ADPRO drawio](https://github.com/user-attachments/assets/8fa5a27f-3290-4a89-89ed-e9f51bd49049)

- Code Diagram - Table Service
![ADPRO-Page-2 drawio](https://github.com/user-attachments/assets/2a5ac5c4-0a17-4ea1-a3dd-7463ad3d3162)
