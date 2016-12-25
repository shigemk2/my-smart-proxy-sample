package com.example

import akka.actor._

object SmartProxyDriver extends CompletableApp(6) {
}

class ServiceProviderProxy(serviceProvider: ActorRef) extends Actor {
  val requesters = scala.collection.mutable.Map[String, ActorRef]()

  def receive = {
    case request: ServiceRequest =>
      requesters(request.requestId) = sender()
      serviceProvider ! request
      analyzeRequest(request)
    case reply: ServiceReply =>
      val sender = requesters.remove(reply.replyId)
      if (sender.isDefined) {
        analyzeReply(reply)
        sender.get ! reply
      }
  }

  def analyzeReply(reply: ServiceReply) = {
    println(s"Reply analyzed: $reply")
  }

  def analyzeRequest(request: ServiceRequest) = {
    println(s"Request analyzed: $request")
  }
}

case class RequestService(service: ServiceRequest)

trait ServiceRequest {
  def requestId: String
}

case class ServiceRequestOne(requestId: String) extends ServiceRequest
case class ServiceRequestTwo(requestId: String) extends ServiceRequest
case class ServiceRequestThree(requestId: String) extends ServiceRequest

trait ServiceReply {
  def replyId: String
}

case class ServiceReplyOne(replyId: String) extends ServiceReply
case class ServiceReplyTwo(replyId: String) extends ServiceReply
case class ServiceReplyThree(replyId: String) extends ServiceReply

class ServiceProvider extends Actor {
  def receive = {
    case one: ServiceRequestOne =>
      sender ! ServiceReplyOne(one.requestId)
    case two: ServiceRequestTwo =>
      sender ! ServiceReplyTwo(two.requestId)
    case three: ServiceRequestThree =>
      sender ! ServiceReplyThree(three.requestId)
  }
}