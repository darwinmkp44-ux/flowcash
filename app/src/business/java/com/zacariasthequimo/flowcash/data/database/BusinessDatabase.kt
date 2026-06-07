package com.zacariasthequimo.flowcash.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.zacariasthequimo.flowcash.data.dao.CustomerDao
import com.zacariasthequimo.flowcash.data.dao.DebtDao
import com.zacariasthequimo.flowcash.data.dao.ProductDao
import com.zacariasthequimo.flowcash.data.dao.SaleDao
import com.zacariasthequimo.flowcash.data.dao.TeamMemberDao
import com.zacariasthequimo.flowcash.data.entity.Customer
import com.zacariasthequimo.flowcash.data.entity.Debt
import com.zacariasthequimo.flowcash.data.entity.Product
import com.zacariasthequimo.flowcash.data.entity.Sale
import com.zacariasthequimo.flowcash.data.entity.TeamMember

@Database(
    entities = [Customer::class, Product::class, Sale::class, Debt::class, TeamMember::class],
    version = 1,
    exportSchema = false
)
abstract class BusinessDatabase : RoomDatabase() {
    abstract fun customerDao(): CustomerDao
    abstract fun productDao(): ProductDao
    abstract fun saleDao(): SaleDao
    abstract fun debtDao(): DebtDao
    abstract fun teamMemberDao(): TeamMemberDao

    companion object {
        @Volatile
        private var INSTANCE: BusinessDatabase? = null

        fun getDatabase(context: Context): BusinessDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BusinessDatabase::class.java,
                    "flowcash_business_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
