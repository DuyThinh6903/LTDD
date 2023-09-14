package com.example.myapplication

open class Aquarium (open var length: Int = 100, open var width: Int = 20, open var height: Int = 40) {

//    var length: Int = length
//    var width: Int = width
//    var height: Int = height

    open var volume: Int
        get() = width * height * length / 1000
        set(value) {
            height = (value * 1000) / (width * length)
        }
    open val shape = "rectangle"
    open var water: Double = 0.0
        get() = volume * 0.9
    init {
        println("aquarium initializing")
    }
    //    init {
//        println("Volume: ${width * length * height / 1000} liters")
//    }
    fun printSize() {
        println(shape)
        println("Width: $width cm " +
                "Length: $length cm " +
                "Height: $height cm ")

        println("Volume: $volume liters Water: $water liters (${water / volume * 100.0}% full)")
    }

    constructor(numberOfFish: Int) : this() {
        val tank = numberOfFish * 2000 * 1.1
        height = (tank / (length * width)).toInt()
    }

}