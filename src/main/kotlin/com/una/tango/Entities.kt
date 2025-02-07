package com.una.tango

import jakarta.persistence.*
import java.util.*

// Roles
@Entity
@Table(name = "role")
data class Role(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    var name: String,
    // Entity Relationship
    @ManyToMany
    @JoinTable(
        name = "role_privilege",
        joinColumns = [JoinColumn(name = "role_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "privilege_id", referencedColumnName = "id")]
    )
    var privilegeList: Set<Privilege>,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Role) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "Role(id=$id, name='$name', privilegeList=$privilegeList)"
    }
}

@Entity
@Table(name = "privilege")
data class Privilege(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    var name: String,
    // Entity Relationship
    @ManyToMany(fetch = FetchType.LAZY)
    var userList: Set<User>,
    @ManyToMany(fetch = FetchType.LAZY)
    var roleList: Set<Role>,

    ) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Privilege) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "Privilege(id=$id, name='$name', userList=$userList, roleList=$roleList)"
    }
}


// Usuarios
@Entity
@Table(name = "tango_user")
data class User (
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id : Long? = null,
    @Column
    var firstName: String? = null,
    @Column
    var lastName: String? = null,
    @Column
    var password: String? = null,
    @Column(unique = true)
    var email: String? = null,
    @Column
    var createDate: Date? = null,
    @Column
    var enabled : Boolean = false,
    @Column
    var tokenExpired : Boolean = false,
    @Column
    var address : String? = null,
    @OneToMany(mappedBy = "client")
    var orders: List<Order>? = null,

    @ManyToMany
    @JoinTable(
        name = "user_role",
        joinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "role_id", referencedColumnName = "id")]
    )
    var roleList: Set<Role>? = null,

){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is User) return false

        if (id != other.id) return false
        if (email != other.email) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + email.hashCode()
        return result
    }

    override fun toString(): String {
        return "User(id=$id, firstName='$firstName', lastName='$lastName', password='$password', email='$email', createDate=$createDate, enabled=$enabled, tokenExpired=$tokenExpired, roleList=$roleList)"
    }
}

// Etiquetas
@Entity
@Table(name = "tag")
data class Tag(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id : Long? = null,
    @Column
    var name: String? = null,
    @ManyToMany(mappedBy = "tags")
    var products : Set<Product>? = null
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Tag) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "Tag(id=$id, name=$name)"
    }
}
// Productos
@Entity
@Table(name = "product")
data class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id : Long? = null,
    @Column
    var name : String? = null,
    @ManyToOne
    var category : Category? = null,
    @Column
    var price : Double? = null,
    @Column
    var quantity : Long? = null,
    @Column
    var imageUrl: String? = null,
    @Column
    var description : String? = null,
    @OneToMany(mappedBy = "product")
    var orderDetails: List<OrderDetail>? = null,
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "product_tag",
        joinColumns = [JoinColumn(name = "product_id", referencedColumnName = "id") ],
        inverseJoinColumns = [JoinColumn(name = "tag_id", referencedColumnName = "id")]
    )
    var tags : Set<Tag>? = null
    ){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Product) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "Product(id=$id, name=$name, category=$category, price=$price, quantity=$quantity, imageUrl=$imageUrl, description=$description)"
    }
}

// Categorias
@Entity
@Table(name = "category")
data class Category(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id : Long? = null,
    @Column
    var name: String? = null,
    @OneToMany(mappedBy = "category")
    var products: List<Product>? = null
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Category) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "Category(id=$id, name=$name)"
    }
}
// Ordenes
@Entity
@Table(name = "tango_order")
data class Order(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id : Long? = null,
    @Column
    var date: Date? = null,
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id")
    var client: User? = null,
    @Column
    var total: Double? = null,
    @ManyToOne
    @JoinColumn(name = "order_status_id", nullable = false, referencedColumnName = "id")
    var orderStatus: OrderStatus,
    @Column
    var deliveryDate: Date? = null,
    @Column
    var additionalInformation: String? = null,
    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL])
    var orderDetails: List<OrderDetail>? = null
){

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Order) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "Order(id=$id, date=$date, client=$client, total=$total, orderStatus=$orderStatus, deliveryDate=$deliveryDate, additionalInformation=$additionalInformation)"
    }

}

// Detalles de ordenes
@Entity
@Table(name = "order_detail")
data class OrderDetail(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id : Long? = null,
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false, referencedColumnName = "id")
    var order: Order? = null,
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false, referencedColumnName = "id")
    var product: Product,
    @Column
    var quantity: Int? = null,
    @Column
    var price: Double? = null,
    @Column
    var total: Double? = null
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OrderDetail) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "OrderDetail(id=$id, product=$product, quantity=$quantity, price=$price, total=$total)"
    }
}
// Estado de ordenes
@Entity
@Table(name = "order_status")
data class OrderStatus(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id : Long? = null,
    @Column
    var name: String? = null,
    @OneToMany(mappedBy = "orderStatus")
    var orders: List<Order>? = null
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OrderStatus) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "OrderStatus(id=$id, name=$name)"
    }
}