package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.CachedCartDao
import com.example.data.local.dao.CachedProductDao
import com.example.data.local.entities.CachedCartItemEntity
import com.example.data.local.entities.CachedProductEntity
import com.example.data.models.CartItem
import com.example.data.models.NotificationItem
import com.example.data.models.Order
import com.example.data.models.Product
import com.example.data.models.Review
import com.example.data.models.WishlistItem

@Database(
    entities = [
        CachedProductEntity::class,
        CachedCartItemEntity::class,
        Product::class,
        CartItem::class,
        WishlistItem::class,
        Order::class,
        Review::class,
        NotificationItem::class
    ],
    version = 2,
    exportSchema = false
)
abstract class VeloraDatabase : RoomDatabase() {

    abstract fun cachedProductDao(): CachedProductDao
    abstract fun cachedCartDao(): CachedCartDao

    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
    abstract fun wishlistDao(): WishlistDao
    abstract fun orderDao(): OrderDao
    abstract fun reviewDao(): ReviewDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        @Volatile
        private var INSTANCE: VeloraDatabase? = null

        fun getDatabase(context: Context): VeloraDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VeloraDatabase::class.java,
                    "velora_offline_store.db"
                ).fallbackToDestructiveMigration()
                 .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
