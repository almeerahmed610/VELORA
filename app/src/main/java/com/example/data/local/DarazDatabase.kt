package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.models.CartItem
import com.example.data.models.NotificationItem
import com.example.data.models.Order
import com.example.data.models.Product
import com.example.data.models.Review
import com.example.data.models.WishlistItem

@Database(
    entities = [
        Product::class,
        CartItem::class,
        WishlistItem::class,
        Order::class,
        Review::class,
        NotificationItem::class
    ],
    version = 1,
    exportSchema = false
)
abstract class DarazDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
    abstract fun wishlistDao(): WishlistDao
    abstract fun orderDao(): OrderDao
    abstract fun reviewDao(): ReviewDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        @Volatile
        private var INSTANCE: DarazDatabase? = null

        fun getDatabase(context: Context): DarazDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DarazDatabase::class.java,
                    "daraz_local_store.db"
                ).fallbackToDestructiveMigration()
                 .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
