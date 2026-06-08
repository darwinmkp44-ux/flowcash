package com.zacariasthequimo.flowcash.ui

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.zacariasthequimo.flowcash.data.dao.CustomerDao
import com.zacariasthequimo.flowcash.data.dao.DebtDao
import com.zacariasthequimo.flowcash.data.dao.PaymentMethodRevenue
import com.zacariasthequimo.flowcash.data.dao.ProductDao
import com.zacariasthequimo.flowcash.data.dao.SaleDao
import com.zacariasthequimo.flowcash.data.dao.TeamMemberDao
import com.zacariasthequimo.flowcash.data.database.BusinessDatabase
import com.zacariasthequimo.flowcash.data.entity.Customer
import com.zacariasthequimo.flowcash.data.entity.Debt
import com.zacariasthequimo.flowcash.data.entity.Product
import com.zacariasthequimo.flowcash.data.entity.Sale
import com.zacariasthequimo.flowcash.data.entity.TeamMember
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class BusinessViewModel(application: Application) : FinanceViewModel(application) {

    private val businessDb = BusinessDatabase.getDatabase(application)
    val customerDao: CustomerDao = businessDb.customerDao()
    val productDao: ProductDao = businessDb.productDao()
    val saleDao: SaleDao = businessDb.saleDao()
    val debtDao: DebtDao = businessDb.debtDao()
    val teamMemberDao: TeamMemberDao = businessDb.teamMemberDao()

    // Customers
    private val _customers = MutableStateFlow<List<Customer>>(emptyList())
    val customers: StateFlow<List<Customer>> = _customers.asStateFlow()

    private val _customerSearchQuery = MutableStateFlow("")
    val customerSearchQuery: StateFlow<String> = _customerSearchQuery.asStateFlow()

    init {
        viewModelScope.launch { customerDao.getAllCustomers().collect { _customers.value = it } }
    }

    fun searchCustomers(query: String) {
        _customerSearchQuery.value = query
        viewModelScope.launch {
            if (query.isBlank()) {
                customerDao.getAllCustomers().collect { _customers.value = it }
            } else {
                customerDao.searchCustomers(query).collect { _customers.value = it }
            }
        }
    }

    fun addCustomer(name: String, email: String, phone: String, address: String, notes: String) {
        viewModelScope.launch {
            customerDao.insert(Customer(name = name, email = email, phone = phone, address = address, notes = notes))
        }
    }

    fun updateCustomer(customer: Customer) {
        viewModelScope.launch { customerDao.update(customer) }
    }

    fun deleteCustomer(customer: Customer) {
        viewModelScope.launch { customerDao.delete(customer) }
    }

    // Products
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _productSearchQuery = MutableStateFlow("")
    val productSearchQuery: StateFlow<String> = _productSearchQuery.asStateFlow()

    init {
        viewModelScope.launch { productDao.getAllProducts().collect { _products.value = it } }
    }

    fun searchProducts(query: String) {
        _productSearchQuery.value = query
        viewModelScope.launch {
            if (query.isBlank()) {
                productDao.getAllProducts().collect { _products.value = it }
            } else {
                productDao.searchProducts(query).collect { _products.value = it }
            }
        }
    }

    fun addProduct(name: String, description: String, price: Double, cost: Double, stockQty: Int, category: String, photoPath: String = "") {
        viewModelScope.launch {
            productDao.insert(Product(name = name, description = description, price = price, cost = cost, stockQty = stockQty, category = category, photoPath = photoPath))
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch { productDao.update(product) }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch { productDao.delete(product) }
    }

    // Sales
    private val _sales = MutableStateFlow<List<Sale>>(emptyList())
    val sales: StateFlow<List<Sale>> = _sales.asStateFlow()

    init {
        viewModelScope.launch { saleDao.getAllSales().collect { _sales.value = it } }
    }

    fun addSale(
        customerId: Long?,
        customerName: String,
        itemsJson: String,
        subtotal: Double,
        discount: Double,
        total: Double,
        paymentMethod: String
    ) {
        viewModelScope.launch {
            saleDao.insert(Sale(
                customerId = customerId,
                customerName = customerName,
                itemsJson = itemsJson,
                subtotal = subtotal,
                discount = discount,
                total = total,
                paymentMethod = paymentMethod
            ))
        }
    }

    fun deleteSale(sale: Sale) {
        viewModelScope.launch { saleDao.delete(sale) }
    }

    // Dashboard stats
    private val _todayRevenue = MutableStateFlow(0.0)
    val todayRevenue: StateFlow<Double> = _todayRevenue.asStateFlow()

    private val _monthRevenue = MutableStateFlow(0.0)
    val monthRevenue: StateFlow<Double> = _monthRevenue.asStateFlow()

    private val _monthSalesCount = MutableStateFlow(0)
    val monthSalesCount: StateFlow<Int> = _monthSalesCount.asStateFlow()

    private val _totalCustomers = MutableStateFlow(0)
    val totalCustomers: StateFlow<Int> = _totalCustomers.asStateFlow()

    private val _pendingDebtAmount = MutableStateFlow(0.0)
    val pendingDebtAmount: StateFlow<Double> = _pendingDebtAmount.asStateFlow()

    private val _grossProfit = MutableStateFlow(0.0)
    val grossProfit: StateFlow<Double> = _grossProfit.asStateFlow()

    private val _paymentMethodRevenue = MutableStateFlow<List<PaymentMethodRevenue>>(emptyList())
    val paymentMethodRevenue: StateFlow<List<PaymentMethodRevenue>> = _paymentMethodRevenue.asStateFlow()

    fun refreshDashboard() {
        viewModelScope.launch {
            val cal = Calendar.getInstance()
            cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
            val todayStart = cal.timeInMillis
            cal.set(Calendar.DAY_OF_MONTH, 1)
            val monthStart = cal.timeInMillis
            cal.add(Calendar.MONTH, -1)
            val lastMonthStart = cal.timeInMillis

            val todayRev = saleDao.getTotalRevenueSince(todayStart) ?: 0.0
            val monthRev = saleDao.getTotalRevenueSince(monthStart) ?: 0.0
            val monthCount = saleDao.getSalesCountInRange(monthStart, System.currentTimeMillis())
            val monthSubtotal = saleDao.getTotalSubtotalInRange(monthStart, System.currentTimeMillis()) ?: 0.0

            _todayRevenue.value = todayRev
            _monthRevenue.value = monthRev
            _monthSalesCount.value = monthCount
            _totalCustomers.value = customers.value.size
            _pendingDebtAmount.value = debtDao.getTotalPendingAmount() ?: 0.0

            // Gross profit: estimate (subtotal as proxy for revenue) - cost
            // For a simple estimate, use monthSubtotal as revenue
            _grossProfit.value = monthRev - (monthSubtotal * 0.6)

            val paymentRev = saleDao.getRevenueByPaymentMethod(monthStart, System.currentTimeMillis())
            _paymentMethodRevenue.value = paymentRev
        }
    }

    // Debts
    private val _debts = MutableStateFlow<List<Debt>>(emptyList())
    val debts: StateFlow<List<Debt>> = _debts.asStateFlow()

    init {
        viewModelScope.launch { debtDao.getAllDebts().collect { _debts.value = it } }
    }

    fun addDebt(customerId: Long?, customerName: String, amount: Double, description: String, dueDate: Long) {
        viewModelScope.launch {
            debtDao.insert(Debt(customerId = customerId, customerName = customerName, amount = amount, description = description, dueDate = dueDate))
        }
    }

    fun payDebt(debt: Debt, paymentAmount: Double) {
        viewModelScope.launch {
            val newPaid = debt.paidAmount + paymentAmount
            val isFullyPaid = newPaid >= debt.amount
            debtDao.update(debt.copy(paidAmount = newPaid, paid = isFullyPaid))
        }
    }

    fun deleteDebt(debt: Debt) {
        viewModelScope.launch { debtDao.delete(debt) }
    }

    // Team members
    private val _teamMembers = MutableStateFlow<List<TeamMember>>(emptyList())
    val teamMembers: StateFlow<List<TeamMember>> = _teamMembers.asStateFlow()

    init {
        viewModelScope.launch { teamMemberDao.getAllMembers().collect { _teamMembers.value = it } }
    }

    fun addTeamMember(name: String, email: String, role: String, permissions: String) {
        viewModelScope.launch {
            teamMemberDao.insert(TeamMember(name = name, email = email, role = role, permissions = permissions))
        }
    }

    fun updateTeamMember(member: TeamMember) {
        viewModelScope.launch { teamMemberDao.update(member) }
    }

    fun deleteTeamMember(member: TeamMember) {
        viewModelScope.launch { teamMemberDao.delete(member) }
    }

    // Profit/Loss reports
    suspend fun getProfitLossReport(monthStart: Long, monthEnd: Long): ProfitLossReport {
        val revenue = saleDao.getTotalRevenueInRange(monthStart, monthEnd) ?: 0.0
        val products = productDao.getAllProducts()
        val monthSales = saleDao.getSalesCountInRange(monthStart, monthEnd)
        val debtTotal = debtDao.getTotalPendingAmount() ?: 0.0
        return ProfitLossReport(
            revenue = revenue,
            totalSales = monthSales,
            pendingDebts = debtTotal
        )
    }
}

data class ProfitLossReport(
    val revenue: Double,
    val totalSales: Int,
    val pendingDebts: Double
)
