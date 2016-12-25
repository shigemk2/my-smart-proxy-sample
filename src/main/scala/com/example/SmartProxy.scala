package com.example

import akka.actor._

object SmartProxyDriver extends CompletableApp(6) {
}

case class RequestService(service: ServiceRequest)

trait ServiceRequest {
  def requestId: String
}