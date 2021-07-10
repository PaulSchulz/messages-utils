(ns messages-utils.core
  (:require [clojure.main :as main])
  (:require [messages-utils.queue :as queue])
  (:gen-class))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; The following was taken from:
;; https://github.com/clojure-cookbook/clojure-cookbook/blob/master/05_network-io/5-11_udp.asciidoc

(import '[java.net DatagramSocket
          DatagramPacket
          InetSocketAddress])

(defn udp-send
  "Send a short textual message over a DatagramSocket to the specified
  host and port. If the string is over 512 bytes long, it will be
  truncated."
  [^DatagramSocket socket msg host port]
  (let [payload (.getBytes msg)
        length (min (alength payload) 512)
        address (InetSocketAddress. host port)
        packet (DatagramPacket. payload length address)]
    (.send socket packet)))

(defn udp-receive
  "Block until a UDP message is received on the given DatagramSocket, and
  return the payload message as a string."
  [^DatagramSocket socket]
  (let [buffer (byte-array 512)
        packet (DatagramPacket. buffer 512)]
    (.receive socket packet)
    (String. (.getData packet)
             0 (.getLength packet))))

(defn udp-receive-loop
  "Given a function and DatagramSocket, will (in another thread) wait
  for the socket to receive a message, and whenever it does, will call
  the provided function on the incoming message."
  [socket f]
  (future (while true (f (udp-receive socket)))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def socket (DatagramSocket. 4478))

(defn process-message
  [x]
  (println (str "<-- " x)))

(defn start-rx
  "Start receiving packets"
  []
  (udp-receive-loop socket process-message))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Starting listener...")
  (println "  To test, use: (udp-send socket \"Hello\" \"localhost\" 4447)")
  (start-rx)
  )
