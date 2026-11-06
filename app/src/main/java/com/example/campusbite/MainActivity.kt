package com.example.campusbite

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private data class FoodItem(
        val name: String,
        val description: String,
        val price: Int,
        val category: String,
        val emoji: String
    )

    private val menuItems = listOf(
        FoodItem("Veg Cheese Sandwich", "Toasted sandwich with cheese", 90, "Snacks", "🥪"),
        FoodItem("Masala Maggi", "Classic spicy masala noodles", 60, "Snacks", "🍜"),
        FoodItem("French Fries", "Crispy salted potato fries", 80, "Snacks", "🍟"),
        FoodItem("Paneer Roll", "Paneer tikka wrapped in a roll", 110, "Meals", "🌯"),
        FoodItem("Veg Burger", "Vegetable patty with fresh salad", 100, "Meals", "🍔"),
        FoodItem("Veg Fried Rice", "Wok-tossed rice and vegetables", 120, "Meals", "🍚"),
        FoodItem("Cold Coffee", "Chilled creamy coffee", 75, "Drinks", "🥤"),
        FoodItem("Lemon Soda", "Refreshing lemon drink", 45, "Drinks", "🍋"),
        FoodItem("Chocolate Brownie", "Soft chocolate brownie", 70, "Desserts", "🍫")
    )

    private val cart = mutableMapOf<String, Int>()
    private var selectedCategory = "All"
    private lateinit var menuContainer: LinearLayout
    private lateinit var cartText: TextView
    private lateinit var searchInput: EditText
    private lateinit var database: OrderDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database = OrderDatabaseHelper(this)
        menuContainer = findViewById(R.id.menuContainer)
        cartText = findViewById(R.id.cartText)
        searchInput = findViewById(R.id.searchInput)

        setupCategoryButtons()
        setupSearch()
        findViewById<Button>(R.id.placeOrderButton).setOnClickListener { placeOrder() }
        findViewById<Button>(R.id.historyButton).setOnClickListener { showOrderHistory() }
        displayMenu()
        updateCartSummary()
    }

    private fun setupCategoryButtons() {
        val categories = listOf("All", "Snacks", "Meals", "Drinks", "Desserts")
        val categoryContainer = findViewById<LinearLayout>(R.id.categoryContainer)
        for (category in categories) {
            val button = Button(this)
            button.text = category
            button.isAllCaps = false
            button.setOnClickListener {
                selectedCategory = category
                displayMenu()
            }
            categoryContainer.addView(button)
        }
    }

    private fun setupSearch() {
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                displayMenu()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun displayMenu() {
        menuContainer.removeAllViews()
        val query = searchInput.text.toString().trim().lowercase(Locale.getDefault())
        var visibleCount = 0

        for (item in menuItems) {
            val categoryMatches = selectedCategory == "All" || item.category == selectedCategory
            val searchMatches = item.name.lowercase(Locale.getDefault()).contains(query)
            if (categoryMatches && searchMatches) {
                menuContainer.addView(createFoodCard(item))
                visibleCount++
            }
        }

        if (visibleCount == 0) {
            val emptyText = TextView(this)
            emptyText.text = "No food items found. Try another search."
            emptyText.setPadding(16, 24, 16, 24)
            menuContainer.addView(emptyText)
        }
    }

    private fun createFoodCard(item: FoodItem): View {
        val card = LinearLayout(this)
        card.orientation = LinearLayout.VERTICAL
        card.setPadding(18, 14, 18, 14)
        card.setBackgroundResource(android.R.drawable.dialog_holo_light_frame)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 0, 0, 14)
        card.layoutParams = params

        val title = TextView(this)
        title.text = "${item.emoji}  ${item.name}"
        title.textSize = 18f
        title.setTextColor(getColor(R.color.campus_text))
        card.addView(title)

        val description = TextView(this)
        description.text = item.description
        description.textSize = 13f
        description.setTextColor(getColor(R.color.campus_muted))
        card.addView(description)

        val bottomRow = LinearLayout(this)
        bottomRow.orientation = LinearLayout.HORIZONTAL
        bottomRow.gravity = Gravity.CENTER_VERTICAL
        bottomRow.setPadding(0, 8, 0, 0)

        val price = TextView(this)
        price.text = "₹${item.price}"
        price.textSize = 17f
        price.setTextColor(getColor(R.color.campus_green))
        bottomRow.addView(price, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        val addButton = Button(this)
        addButton.text = if ((cart[item.name] ?: 0) > 0) "Add more" else "Add +"
        addButton.isAllCaps = false
        addButton.setOnClickListener {
            cart[item.name] = (cart[item.name] ?: 0) + 1
            updateCartSummary()
            addButton.text = "Add more"
            Toast.makeText(this, "${item.name} added to cart", Toast.LENGTH_SHORT).show()
        }
        bottomRow.addView(addButton)
        card.addView(bottomRow)
        return card
    }

    private fun updateCartSummary() {
        var itemCount = 0
        var total = 0
        for (item in menuItems) {
            val quantity = cart[item.name] ?: 0
            itemCount += quantity
            total += quantity * item.price
        }
        cartText.text = "Cart: $itemCount item(s)  •  Total: ₹$total"
    }

    private fun placeOrder() {
        if (cart.isEmpty()) {
            Toast.makeText(this, "Please add an item first", Toast.LENGTH_SHORT).show()
            return
        }

        val orderLines = StringBuilder()
        var total = 0
        for (item in menuItems) {
            val quantity = cart[item.name] ?: 0
            if (quantity > 0) {
                orderLines.append("${item.name} x$quantity\n")
                total += item.price * quantity
            }
        }

        AlertDialog.Builder(this)
            .setTitle("Confirm your order?")
            .setMessage("$orderLines\nTotal: ₹$total")
            .setPositiveButton("Place order") { _, _ ->
                val time = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date())
                database.saveOrder(orderLines.toString().trim(), total.toDouble(), time)
                cart.clear()
                updateCartSummary()
                displayMenu()
                Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_LONG).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showOrderHistory() {
        AlertDialog.Builder(this)
            .setTitle("Your order history")
            .setMessage(database.getOrderHistory())
            .setPositiveButton("Close", null)
            .show()
    }
}
