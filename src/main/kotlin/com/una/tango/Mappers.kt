package com.una.tango

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.ReportingPolicy
import java.util.*

// componentModel = "spring" añadido desde build.gradle.kts

@Mapper(imports = [Date::class],
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses=[RoleMapper::class])
interface UserMapper{
    @Mapping(source = "roleList", target = "roleList")
    fun userToUserResult(user:User):UserResult
    fun userListToUserResultList(userList:List<User>):List<UserResult>

    @Mapping(target ="createDate", expression ="java(new java.util.Date())")
    @Mapping(target ="enabled", expression ="java(true)")
    @Mapping(target ="tokenExpired", expression ="java(false)")
    fun userSignUpInputToUser(dto: UserSignUpInput):User
}

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface RoleMapper {
    fun roleListToRoleDetailsList(
        roleList: Set<Role>?
    ): Set<RoleDetails>
    fun roleInputListToRoleSet(
        roleList: List<RoleInput>?
    ): Set<Role>
}

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface TagMapper{
    fun tagToTagResult(tag:Tag):TagResult
    fun tagInputToTag(tagInput:TagInput):Tag
    fun tagListToTagResultList(tagList:List<Tag>):List<TagResult>
    fun tagInputListToTagList(tagInputList:List<TagInput>):List<Tag>
}

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, uses=[CategoryMapper::class, TagMapper::class])
interface ProductMapper{
    @Mapping(target = "category.products", ignore = true)
    @Mapping(target = "tags.products", ignore = true)
    fun productToProductResult(product:Product):ProductResult
    fun productInputToProduct(productInput:ProductInput):Product
    fun productListToProductResultList(productList:List<Product>):List<ProductResult>
    fun productInputListToProductList(productInputList:List<ProductInput>):List<Product>
}

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface CategoryMapper{
    @Mapping(target = "products", ignore = true)
    fun categoryToCategoryResult(category:Category):CategoryResult

    fun categoryInputToCategory(categoryInput:CategoryInput):Category

    @Mapping(target = "products", ignore = true)
    fun categoryListToCategoryResultList(categoryList:List<Category>):List<CategoryResult>

    fun categoryInputListToCategoryList(categoryInputList:List<CategoryInput>):List<Category>
}
@Mapper(imports = [Date::class],
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses=[RoleMapper::class])
interface OrderMapper{
    fun orderToOrderResult(order:Order):OrderResult
    @Mapping(target ="date", expression ="java(new java.util.Date())")
    fun orderInputToOrder(orderInput:OrderInput):Order
    fun orderListToOrderResultList(orderList:List<Order>):List<OrderResult>
    fun orderInputListToOrderList(orderInputList:List<OrderInput>):List<Order>
}

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface OrderStatusMapper{
    fun orderStatusToOrderStatusResult(orderStatus:OrderStatus):OrderStatusResult
    fun orderStatusInputToOrderStatus(orderStatusInput:OrderStatusInput):OrderStatus
    fun orderStatusListToOrderStatusResultList(orderStatusList:List<OrderStatus>):List<OrderStatusResult>
    fun orderStatusInputListToOrderStatusList(orderStatusInputList:List<OrderStatusInput>):List<OrderStatus>
}

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface OrderDetailMapper{
    fun orderDetailToOrderDetailResult(orderDetail:OrderDetail):OrderDetailResult
    fun orderDetailInputToOrderDetail(orderDetailInput:OrderDetailInput):OrderDetail
    fun orderDetailListToOrderDetailResultList(orderDetailList:List<OrderDetail>):List<OrderDetailResult>
    fun orderDetailInputListToOrderDetailList(orderDetailInputList:List<OrderDetailInput>):List<OrderDetail>
}

