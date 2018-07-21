package com.prvz.url_checker.config

import com.mongodb.MongoClient
import com.prvz.url_checker.converter.DateToOffsetDateTimeConverter
import com.prvz.url_checker.converter.OffsetDateTimeToDateConverter
import org.springframework.context.annotation.Configuration
import org.springframework.data.convert.CustomConversions
import org.springframework.data.mongodb.config.AbstractMongoConfiguration


@Configuration
open class MongoConfig : AbstractMongoConfiguration() {

    override fun getDatabaseName(): String = "db"

    override fun mongoClient(): MongoClient = MongoClient("127.0.0.1", 27017)

    override fun getMappingBasePackages(): MutableCollection<String> = mutableListOf("com.prvz")

    override fun customConversions(): CustomConversions {
        return CustomConversions(CustomConversions.StoreConversions.NONE,
                listOf(OffsetDateTimeToDateConverter(),
                        DateToOffsetDateTimeConverter()))
    }
}