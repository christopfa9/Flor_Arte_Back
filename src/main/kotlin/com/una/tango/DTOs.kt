package com.una.tango

import java.util.*

// Roles y privilegios

data class PrivilegeDetails(
    var id: Long? = null,
    var name: String? = null,
)

data class RoleInput(
    var id: Long? = null,
    var name: String = ""
)

data class RoleDetails(
    var id: Long? = null,
    var name: String? = null,
    var privileges: List<PrivilegeDetails>? = null,
)

// Usuarios

data class UserInput(
    var id: Long? = null
)

data class UserLoginInput(
    var username: String = "",
    var password: String = "",
)

data class UserSignUpInput(
    var firstName: String? = null,
    var lastName: String? = null,
    var email: String? = null,
    var password: String? = null,
    var address: String? = null,
    var roles: List<RoleInput>? = null
)

data class UserUpdateInput(
    var firstName: String? = null,
    var lastName: String? = null,
    var email: String? = null,
    var password: String? = null,
    var address: String? = null,
    var enabled: Boolean? = null,
    var roles: List<RoleInput>? = null
)

data class UserResult(
    var id: Long,
    var firstName: String,
    var lastName: String,
    var email: String,
    var enabled: Boolean?,
    var tokenExpired: Boolean?,
    var createDate: Date,
    var roleList: List<RoleDetails>,
    var address: String?,
)

// Tags

data class TagInput(
    var id: Long? = null,
    var name: String? = null,
)

data class TagResult(
    var id: Long? = null,
    var name: String? = null,
)

// Categorias

data class CategoryInput(
    var id: Long? = null,
    var name: String? = null,
)

data class CategoryResult(
    var id: Long? = null,
    var name: String? = null,
    var products: List<ProductResult>? = null,
)

// Estado de orden

data class OrderStatusInput(
    var id: Long? = null,
    var name: String? = null,
)

data class OrderStatusResult(
    var id: Long? = null,
    var name: String? = null,
    var orders: List<OrderResult>? = null,
)

// Detalle de orden

data class OrderDetailInput(
    var id: Long? = null,
    //var order: OrderInput? = null,
    var product: ProductInput? = null,
    var quantity: Int? = null,
    var price: Double? = null,
    var total: Double? = null,
)

data class OrderDetailResult(
    var id: Long? = null,
   // var order: OrderInput? = null,
    var product: ProductInput? = null,
    var quantity: Int? = null,
    var price: Double? = null,
    var total: Double? = null,
)

//Ordenes

data class OrderInput(
    var id: Long? = null,
    var date: Date? = null,
    var deliveryDate: Date? = null,
    var client: UserInput? = null,
    var total: Double? = null,
    var additionalInformation: String? = null,
    var orderStatus: OrderStatusInput? = null,
    var orderDetails: List<OrderDetailInput>? = null,
)

data class OrderResult(
    var id: Long? = null,
    var date: Date? = null,
    var deliveryDate: Date? = null,
    var client: UserInput? = null,
    var total: Double? = null,
    var additionalInformation: String? = null,
    var orderStatus: OrderStatusInput? = null,
    var orderDetails: List<OrderDetailResult>? = null,
)

// Productos

data class ProductInput(
    var id: Long? = null,
    var name: String? = null,
    var description: String? = null,
    var price: Double? = null,
    var quantity: Long? = null,
    var category: CategoryInput? = null,
    var tags: List<TagInput>? = null,
    var imageUrl: String? = null
)

data class ProductResult(
    var id: Long?= null,
    var name: String?= null,
    var description: String?= null,
    var price: Double?= null,
    var quantity: Long?= null,
    var category: CategoryResult? = null,
    var tags: List<TagResult>? = null,
    var imageUrl: String? = null
)
