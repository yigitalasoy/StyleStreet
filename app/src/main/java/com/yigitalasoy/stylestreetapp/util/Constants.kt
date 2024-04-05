package com.yigitalasoy.stylestreetapp.util

object Constants {
    const val FIRESTORE_DATABASE_USERS = "tbl_User"
    const val FIRESTORE_DATABASE_CATEGORIES = "tbl_Category"
    const val FIRESTORE_DATABASE_PRODUCTS = "tbl_Product"
    const val FIRESTORE_DATABASE_COLORS = "tbl_ProductColor"
    const val FIRESTORE_DATABASE_PRODUCT_SIZE = "tbl_ProductSize"
    const val FIRESTORE_DATABASE_BASKET = "tbl_Basket"

    //Category Model constant
    const val CATEGORYRESPONSE_categoryId = "Category_Id"
    const val CATEGORYRESPONSE_categoryName = "Category_Name"
    const val CATEGORYRESPONSE_categoryImage = "Category_Image"


    //product model constant
    const val PRODUCTRESPONSE_productId = "Product_Id"
    const val PRODUCTRESPONSE_productName = "Product_Name"
    const val PRODUCTRESPONSE_categoryId = "Category_Id"

    //subproduct model constant
    const val SUBPRODUCTRESPONSE_subProductId = "SubProduct_Id"
    const val SUBPRODUCTRESPONSE_subProductSizeId = "SubProduct_SizeId"
    const val SUBPRODUCTRESPONSE_subProductStock = "SubProduct_Stock"
    const val SUBPRODUCTRESPONSE_subProductName = "SubProduct_Name"
    const val SUBPRODUCTRESPONSE_subProductColorId = "SubProduct_ColorId"
    const val SUBPRODUCTRESPONSE_subProductPrice = "SubProduct_Price"
    const val SUBPRODUCTRESPONSE_updateTime = "UpdateTime"
    const val SUBPRODUCTRESPONSE_subProductImageURL = "SubProduct_ImageURL"


    //product color model constant
    const val PRODUCTCOLORRESPONSE_colorId = "Color_Id"
    const val PRODUCTCOLORRESPONSE_colorName = "Color_Name"
    const val PRODUCTCOLORRESPONSE_colorCode = "Color_Code"

    //product size model constant
    const val PRODUCTSIZERESPONSE_productSizeId = "ProductSize_Id"
    const val PRODUCTSIZERESPONSE_productSizeName = "ProductSize_Name"



    const val STORAGE_USER_IMAGES = ""
    const val STORAGE_CATEGORY_IMAGES = ""
    const val GOOGLE_SIGN_IN_REQUEST_CODE = 234

}