package dev.fredag.cheerwithme.service

import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sns.SnsClient

fun initAwsSdkClients() {
    AwsSNSFactory.init()
}

object AwsSNSFactory {
    private lateinit var snsClient: SnsClient

    fun init() {
        snsClient = SnsClient
            .builder()
            .region(Region.EU_CENTRAL_1)
            .build()
    }

    fun snsClient(): SnsClient {
        return snsClient;
    }
}