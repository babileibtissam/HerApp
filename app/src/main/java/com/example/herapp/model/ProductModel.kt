package com.example.herapp.model

class ProductModel {
    var key:String?=null
    var name:String?=null
    var image:String?=null
    var description:String?=null
    var price:String?=null

    constructor() // Required for Firebase

    constructor(key: String?, name: String?, image: String?, description: String?, price: String?) {
        this.key = key
        this.name = name
        this.image = image
        this.description = description
        this.price = price
    }
}