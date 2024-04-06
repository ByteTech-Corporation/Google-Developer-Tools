# Google-Developer-Tools
Google Developer Tools is a GDK (Google Developer Kit). 

                                                                    ![gdk](https://github.com/ByteTech-Corporation/Google-Developer-Tools/assets/111024718/0c4c2d0f-b8a5-4766-a230-ac2f8ddc003f)

To help you with paramaters or commands, run `./help.sh` for Linux (`/usr/bin/bash` or `/bin/sh`) or macOS, and for Windows, run `cmd.exe` and then type in `help_gdk`.                                                                   

> Google Developer Kit (planning to develop it with Google)

GDK (Google Developer Kit) Platform, GDT (Google Developer Tools)

> [!IMPORTANT]
> Check if your OS-specific developer SDK (Software Developement Kit) or DK (Developer Kit) is installed correctly. If it is not installed or is not installed ***properly***, search on the Internet for your OS or developer tools specific SDK or DK.

1. Google APIs that you can use:
`
Java:

    Guava (Google core libraries): com.google.guava
    Protocol Buffers (API for encoding and decoding messages): com.google.protobuf
    Google Cloud BigTable HBase Connector: com.google.cloud.bigtable.hbase
    Google Cloud Dataflow Java SDK: apache-beam:apache-beam-gcp-sdk:java
    Google Cloud Data Loss Prevention: com.google.cloud:dlp
    Google Cloud ML Engine Prediction: com.google.cloud:mlengine
    Google Cloud Messaging: com.google.android.gms:play-services-gcm
    Google Charts: com.google.chart:google-visualization
    Google Web Toolkit: com.google.gwt
    Google RPC: com.google.rpc

C:

    Protocol Buffers Compiler: protobuf-compiler
    Google Cloud TPCC Benchmark: google_tpcc
    LevelDB (Key-value storage engine): leveldb
    OpenCV Google TensorFlow Integration: opencv_contrib-python-google-colab

Python:

    TensorFlow: tensorflow
    TensorFlow Serving: tensorflow-model-server
    TensorFlow Estimator: tensorflow-estimator
    TensorFlow Addons: tensorflow-addons
    TensorFlow Datasets: tensorflow-datasets
    TensorFlow Hub: tensorflow-hub
    TensorFlow Model Analysis: tensorflow-model-analysis
    TensorFlow Metrics: tensorflow-metrics
    TensorFlow Slim: tensorflow-slim
    TensorFlow Keras: tensorflow-keras
    TensorFlow Probability: tensorflow-probability
    TensorFlow Federated: tensorflow-federated
    TensorFlow Agents: tensorflow-agents
    TensorFlow Speech Recognition: tensorflow-speech-recognition
    TensorFlow Text: tensorflow-text
    TensorFlow Datacube: tensorflow-datacube
    TensorFlow Privacy: tensorflow-privacy
    TensorFlow Compatibility: tensorflow-compat
    TensorFlow Monadic Parallelism: tensorflow-monads
    TensorFlow GPU: tensorflow-gpu
    TensorFlow Distribution: tensorflow-distribute
    TensorFlow Serving Python Client: tensorflow-serving-cli
    TensorFlow Serving REST API: tensorflow-serving-rest_api
    TensorFlow Serving Savant: tensorflow-serving-saved_model_cli
    TensorFlow Serving Savant REST API: tensorflow-serving-saved_model_rest_api
    TensorFlow Serving Savant gRPC API: tensorflow-serving-saved_model_grpc_api
    TensorFlow Serving Savant HTTP Gateway: tensorflow-serving-http_gateway
    TensorFlow Serving Savant GRPC Gateway: tensorflow-serving-grpc_gateway
    TensorFlow Serving Savant Authentication: tensorflow-serving-authenticators
    TensorFlow Serving Savant Access Control: tensorflow-serving-access_control
    TensorFlow Serving Savant Health Checker: tensorflow-serving-healthchecker
    TensorFlow Serving Savant Status Server: tensorflow-serving-status_server
    TensorFlow Serving Savant Metrics: tensorflow-serving-metrics
    TensorFlow Serving Savant Config: tensorflow-serving-config
    TensorFlow Serving Savant Utilities: tensorflow-serving-utilities
    TensorFlow Serving Savant Extensions: tensorflow-serving-extensions
    TensorFlow Serving Savant Middlewares: tensorflow-serving-middlewares
    TensorFlow Serving Savant Validators: tensorflow-serving-validators
    TensorFlow Serving Savant Interceptors: tensorflow-serving-interceptors
    TensorFlow Serving Savant Filters: tensorflow-serving-filters
    TensorFlow Serving Savant Loggers: tensorflow-serving-loggers
    TensorFlow Serving Savant Tracing: tensorflow-serving-tracing
    TensorFlow Serving Savant Sampling: tensorflow-serving-sampling
    TensorFlow Serving Savant Rate Limiting: tensorflow-serving-ratelimiter
    TensorFlow Serving Savant Circuit Breaker: tensorflow-serving-circuitbreaker
    TensorFlow Serving Savant Retry: tensorflow-serving-retry
    TensorFlow Serving Savant Timeout: tensorflow-serving-timeout
    TensorFlow Serving Savant Deadline: tensorflow-serving-deadline
    TensorFlow Serving Savant Cache: tensorflow-serving-cache
    TensorFlow Serving Savant Header Filter: tensorflow-serving-headerfilter
    TensorFlow Serving Savant Request Filter: tensorflow-serving-requestfilter
    TensorFlow Serving Savant Response Filter: tensorflow-serving-responsefilter
    TensorFlow Serving Savant Trailing Slash Remover: tensorflow-serving-trailingslashremover
    TensorFlow Serving Savant Content Type Sniffer: tensorflow-serving-contenttypesniffer
    TensorFlow Serving Savant Body Reader: tensorflow-serving-bodyreader
    TensorFlow Serving Savant JSON Parser: tensorflow-serving-jsonparser
    TensorFlow Serving Savant XML Parser: tensorflow-serving-xmlparser
    TensorFlow Serving Savant YAML Parser: tensorflow-serving-yamlparser
    TensorFlow Serving Savant Multipart Parser: tensorflow-serving-multipartparser
    TensorFlow Serving Savant Gzip Decoder: tensorflow-serving-gzipdecoder
    TensorFlow Serving Savant Zlib Decoder: tensorflow-serving-zlibdecoder
    TensorFlow Serving Savant Brotli Decoder: tensorflow-serving-brotlicodec
    TensorFlow Serving Savant Decompressor: tensorflow-serving-decompressor
    TensorFlow Serving Savant Encryptor: tensorflow-serving-encryptor
    TensorFlow Serving Savant Signature Verifier: tensorflow-serving-signatureverifier
    TensorFlow Serving Savant Authentication Token Extractor: tensorflow-serving-tokenextractor
    TensorFlow Serving Savant Authorization Policy Evaluator: tensorflow-serving-policyevaluator
    TensorFlow Serving Savant Audit Logger: tensorflow-serving-auditlogger
    TensorFlow Serving Savant Error Handler: tensorflow-serving-errorhandler
    TensorFlow Serving Savant Shutdown Hook: tensorflow-serving-shuthook
    TensorFlow Serving Savant GracefulShutdownHook: tensorflow-serving-gracefulshutdownhook
    TensorFlow Serving Savant Version Info: tensorflow-serving-versioninfo
    TensorFlow Serving Savant Configuration Manager: tensorflow-serving-configmanager
    TensorFlow Serving Savant Model Manager: tensorflow-serving-modelmanager
    TensorFlow Serving Savant Model Inspector: tensorflow-serving-modelinspector
    TensorFlow Serving Savant Model Updater: tensorflow-serving-modelupdater
    TensorFlow Serving Savant Model Pruner: tensorflow-serving-modelpruner
    TensorFlow Serving Savant Model Converter: tensorflow-serving-modelconverter
    TensorFlow Serving Savant Model Builder: tensorflow-serving-modelbuilder
    TensorFlow Serving Savant Model Pusher: tensorflow-serving-modelpusher
    TensorFlow Serving Savant Model Puller: tensorflow-serving-modelpuller
    TensorFlow Serving Savant Model Archiver: tensorflow-serving-modelarchiver
    TensorFlow Serving Savant Model Unarchiver: tensorflow-serving-modelunarchiver
    TensorFlow Serving Savant Model Serializer: tensorflow-serving-modelserializer
    TensorFlow Serving Savant Model Deserializer: tensorflow-serving-modeldeserializer
    TensorFlow Serving Savant Model Splitter: tensorflow-serving-modelsplitter
    TensorFlow Serving Savant Model Merger: tensorflow-serving-modelmerger
    TensorFlow Serving Savant Model Splitting Helper: tensorflow-serving-modelsplithelper
    TensorFlow Serving Savant Model Merging Helper: tensorflow-serving-modelmerginghelper
    TensorFlow Serving Savant Model Saving Helper: tensorflow-serving-modelsavinghelper
    TensorFlow Serving Savant Model Loading Helper: tensorflow-serving-modelloadinghelper
    TensorFlow Serving Savant Model Validating Helper: tensorflow-serving-modelvalidatinghelper
    TensorFlow Serving Savant Model Initializing Helper: tensorflow-serving-modelinitializinghelper
    TensorFlow Serving Savant Model Cleanup Helper: tensorflow-serving-modelcleanuphelper
    TensorFlow Serving Savant Model Backup Helper: tensorflow-serving-modelbackuphelper
    TensorFlow Serving Savant Model Restoration Helper: tensorflow-serving-modelrestorationhelper
    TensorFlow Serving Savant Model Expiration Helper: tensorflow-serving-modelexpirationhelper
    TensorFlow Serving Savant Model Size Tracker: tensorflow-serving-modelsizetracker
    TensorFlow Serving Savant Model Performance Monitor: tensorflow-serving-modelperformancemonitor
    TensorFlow Serving Savant Model Statistic Collector: tensorflow-serving-modelstatisticscollector
    TensorFlow Serving Savant Model Statistics Reporter: tensorflow-serving-modelstatisticsreporter
    TensorFlow Serving Savant Model Health Checker: tensorflow-serving-modelhealthchecker
    TensorFlow Serving Savant Model Monitor: tensorflow-serving-modelmonitor
    TensorFlow Serving Savant Model Metadata Manager: tensorflow-serving-modelmetadatamanager
    TensorFlow Serving Savant Model Version Manager: tensorflow-serving-modelversionmanager
    TensorFlow Serving Savant Model Serving Manager: tensorflow-serving-modelservingmanager
    TensorFlow Serving Savant Model Serving Controller: tensorflow-serving-modelservingcontroller
    TensorFlow Serving Savant Model Serving Dispatcher: tensorflow-serving-modelservingdispatcher
    TensorFlow Serving Savant Model Serving Handler: tensorflow-serving-modelservinghandler
    TensorFlow Serving Savant Model Serving Adapter: tensorflow-serving-modelservingadapter
    TensorFlow Serving Savant Model Serving Framework: tensorflow-serving-framework
    TensorFlow Serving Savant Model Serving Transport: tensorflow-serving-transport
    TensorFlow Serving Savant Model Serving Utilities: tensorflow-serving-utility
    TensorFlow Serving Savant Model Serving Middleware: tensorflow-serving-middleware
    TensorFlow Serving Savant Model Serving Monitor: tensorflow-serving-monitor
    TensorFlow Serving Savant Model Serving Test: tensorflow-serving-test
    TensorFlow Serving Savant Model Serving Example: tensorflow-serving-example
    TensorFlow Serving Savant Model Serving Grpc: tensorflow-serving-grpc
    TensorFlow Serving Savant Model Serving Http: tensorflow-serving-http
    TensorFlow Serving Savant Model Serving RestApi: tensorflow-serving-restapi
    TensorFlow Serving Savant Model Serving Gin: tensorflow-serving-gin
    TensorFlow Serving Savant Model Serving Echo: tensorflow-serving-echo
    TensorFlow Serving Savant Model Serving Swagger: tensorflow-serving-swagger
    TensorFlow Serving Savant Model Serving Prometheus: tensorflow-serving-prometheus
    TensorFlow Serving Savant Model Serving Jaeger: tensorflow-serving-jaeger
    TensorFlow Serving Savant Model Serving Fluentd: tensorflow-serving-fluentd
    TensorFlow Serving Savant Model Serving Elasticsearch: tensorflow-serving-elasticsearch
    TensorFlow Serving Savant Model Serving Redis: tensorflow-serving-redis
    TensorFlow Serving Savant Model Serving Cassandra: tensorflow-serving-cassandra
    TensorFlow Serving Savant Model Serving MongoDb: tensorflow-serving-mongodb
    TensorFlow Serving Savant Model Serving PostgreSQL: tensorflow-serving-postgresql
    TensorFlow Serving Savant Model Serving MySql: tensorflow-serving-mysql
    TensorFlow Serving Savant Model Serving Sqlite: tensorflow-serving-sqlite
    TensorFlow Serving Savant Model Serving Firestore: tensorflow-serving-firestore
    TensorFlow Serving Savant Model Serving BigQuery: tensorflow-serving-bigquery
    TensorFlow Serving Savant Model Serving PubSub: tensorflow-serving-pubsub
    TensorFlow Serving Savant Model Serving Spanner: tensorflow-serving-spanner
    TensorFlow Serving Savant Model Serving Stackdriver: tensorflow-serving-stackdriver
    TensorFlow Serving Savant Model Serving AppEngine: tensorflow-serving-appengine
    TensorFlow Serving Savant Model Serving FunctionsFramework: tensorflow-serving-functionsframework
    TensorFlow Serving Savant Model Serving Docker: tensorflow-serving-docker
    TensorFlow Serving Savant Model Serving Kubernetes: tensorflow-serving-kubernetes
    TensorFlow Serving Savant Model Serving Istio: tensorflow-serving-istio
    TensorFlow Serving Savant Model Serving EnvoyProxy: tensorflow-serving-envoyproxy
    TensorFlow Serving Savant Model Serving NGINX: tensorflow-serving-nginx
    TensorFlow Serving Savant Model Serving ApacheHttpServer: tensorflow-serving-apachehttpserver
    TensorFlow Serving Savant Model Serving Jetty: tensorflow-serving-jetty
    TensorFlow Serving Savant Model Serving Undertow: tensorflow-serving-undertow
    TensorFlow Serving Savant Model Serving Netty: tensorflow-serving-netty
    TensorFlow Serving Savant Model Serving Vertx: tensorflow-serving-vertx
    TensorFlow Serving Savant Model Serving Micronaut: tensorflow-serving-micronaut
    TensorFlow Serving Savant Model Serving Quarkus: tensorflow-serving-quarkus
    TensorFlow Serving Savant Model Serving SpringBoot: tensorflow-serving-springboot
    TensorFlow Serving Savant Model Serving DropWizard: tensorflow-serving-dropwizard
    TensorFlow Serving Savant Model Serving Jersey: tensorflow-serving-jersey
    TensorFlow Serving Savant Model Serving JaxrsClientFactory: tensorflow-serving-jaxrsclientfactory
    TensorFlow Serving Savant Model Serving JacksonJsonProvider: tensorflow-serving-jacksonjsonprovider
    TensorFlow Serving Savant Model Serving JsonParser: tensorflow-serving-jsonparser
    TensorFlow Serving Savant Model Serving MessagePackEncoder: tensorflow-serving-messagepackencoder
    TensorFlow Serving Savant Model Serving MessagePackDecoder: tensorflow-serving-messagepackdecoder
    TensorFlow Serving Savant Model Serving AvroSerializer: tensorflow-serving-avroserializer
    TensorFlow Serving Savant Model Serving AvroDeserializer: tensorflow-serving-avodeserializer
    TensorFlow Serving Savant Model Serving ThriftSerializer: tensorflow-serving-thriftserializer
    TensorFlow Serving Savant Model Serving ThriftDeserializer: tensorflow-serving-thriftsdeserializer
    TensorFlow Serving Savant Model Serving ProtobufSerializer: tensorflow-serving-protobufserializer
    TensorFlow Serving Savant Model Serving ProtobufDeserializer: tensorflow-serving-protodeserializer
    TensorFlow Serving Savant Model Serving ProtocolBuffers: tensorflow-serving-protocolbuffers
    TensorFlow Serving Savant Model Serving BinaryProtocol: tensorflow-serving-binaryprotocol
    TensorFlow Serving Savant Model Serving TextLineProtocol: tensorflow-serving-textlineprotocol
    TensorFlow Serving Savant Model Serving PlainTextProtocol: tensorflow-serving-plaintextprotocol
    TensorFlow Serving Savant Model Serving LineBasedFrameDecoder: tensorflow-serving-linebasedframedecoder
    TensorFlow Serving Savant Model Serving LengthFieldBasedFrameDecoder: tensorflow-serving-lengthfieldbasedframedecoder
    TensorFlow Serving Savant Model Serving FixedLengthFrameDecoder: tensorflow-serving-fixedlengthframedecoder
    TensorFlow Serving Savant Model Serving ByteArrayDecoder: tensorflow-serving-bytearraydecoder
    TensorFlow Serving Savant Model Serving StringDecoder: tensorflow-serving-stringdecoder
    TensorFlow Serving Savant Model Serving ChunkedInputStream: tensorflow-serving-chunkedinputstream
    TensorFlow Serving Savant Model Serving DefaultFileDescriptorSupplier: tensorflow-serving-defaultfiledescriptorsupplyer
    TensorFlow Serving Savant Model Serving FileDescriptorSupplier: tensorflow-serving-filedescriptorsupplyer
    TensorFlow Serving Savant Model Serving ChannelInitializer: tensorflow-serving-channelinitializer
    TensorFlow Serving Savant Model Serving SimpleChannelUpgradeHandler: tensorflow-serving-simplechannelupgradem handler
    TensorFlow Serving Savant Model Serving WebSocketHandler: tensorflow-serving-websockethandler
    TensorFlow Serving Savant Model Serving ServerWebSocketHandler: tensorflow-serving-serverwebsockethandler
    TensorFlow Serving Savant Model Serving WebSocketEncodedFrameDecoder: tensorflow-serving-websoke tencodedframedecoder
    TensorFlow Serving Savant Model Serving WebSocketTextFrameDecoder: tensorflow-serving-websockettextframe decoder
    TensorFlow Serving Savant Model Serving WebSocketBinaryFrameDecoder: tensorflow-serving- websocketbinaryframe decoder
    TensorFlow Serving Savant Model Serving WebSocketFrameAggregator: tensorflow-serving-websocketframeaggregator
    TensorFlow Serving Savant Model Serving WebSocketCompressionHandler: tensorflow-serving-websocketcompressionhandler
    TensorFlow Serving Savant Model Serving CompressionHandler: tensorflow-serving- compressionhandler
    TensorFlow Serving Savant Model Serving HttpResponse: tensorflow-serving-httpre sponse
    TensorFlow Serving Savant Model Serving StatusCode: tensorflow-serving-statuscode
    TensorFlow Serving Savant Model Serving MediaType: tensorflow-serving-mediatype
    TensorFlow Serving Savant Model Serving Headers: tensorflow-serving-headers
    TensorFlow Serving Savant Model Serving Cookies: tensorflow-serving-cookies
    TensorFlow Serving Savant Model Serving QueryParams: tensorflow-serving- queryparams
    TensorFlow Serving Savant Model Serving PathInfo: tensorflow-serving-pathinfo
    TensorFlow Serving Savant Model Serving Method: tensorflow-serving-method
    TensorFlow Serving Savant Model Serving RequestUri: tensorflow-serving- requesturi
    TensorFlow Serving Savant Model Serving RemoteAddress: tensorflow-serving-remoteaddress
    TensorFlow Serving Savant Model Serving Attributes: tensorflow-serving-attributes
    TensorFlow Serving Savant Model Serving HttpRequest: tensorflow-serving-ht tprequest
    TensorFlow Serving Savant Model Serving MultipartFormDataContent: tensorflow-serving-multipartformdatacontent
    TensorFlow Serving Savant Model Serving FormDataBodyPart: tensorflow-serving- formdatabodypart
    TensorFlow Serving Savant Model Serving ContentDisposition: tensorflow-serving-contentdisposition
    TensorFlow Serving Savant Model Serving StreamingMultipartConsumer: tensorflow-serving- streamingmultipartconsumer
    TensorFlow Serving Savant Model Serving StreamingMultipartWriter: tensorflow-serving- stream ingmultipartwriter
    TensorFlow Serving Savant Model Serving InputStreamReader: tensorflow-serving- inputstreamreader
    TensorFlow Serving Savant Model Serving OutputStreamWriter: tensorflow-serving- outputstreamwriter
    TensorFlow Serving Savant Model Serving HttpMethod: tensorflow-serving- httpmethod
    TensorFlow Serving Savant Model Serving HttpVersion: tensorflow-serving-httpversion
    TensorFlow Serving Savant Model Serving HttpHeaders: tensorflow-serving-http headers
    TensorFlow Serving Savant Model Serving HttpContext: tensorflow-serving-httpcontext
    TensorFlow Serving Savant Model Serving HttpResponseBuilder: tensorflow-serving- ht tresponsebuilder
    TensorFlow Serving Savant Model Serving HttpStatusCodes: tensorflow-serving-http statuscodes
    TensorFlow Serving Savant Model Serving HttpMediaTypes: tensorflow-serving-http medi atypes
    TensorFlow Serving Savant Model Serving HttpMethods: tensorflow-serving-http methods
    TensorFlow Serving Savant Model Serving HttpVersions: tensorflow-serving-http versions
    TensorFlow Serving Savant Model Serving HttpCookieContainer: tensorflow-serving-httpcookiecontainer
    TensorFlow Serving Savant Model Serving Cookie: tensorflow-serving- cookie
    TensorFlow Serving Savant Model Serving UriTemplate: tensorflow-serving-uritemplate
    TensorFlow Serving Savant Model Serving RouteCollection: tensorflow-serving-routecollection
    TensorFlow Serving Savant Model Serving RouterBase: tensorflow-serving-routerbase
    TensorFlow Serving Savant Model Serving DelegatingHandler: tensorflow-serving- delegatinghandler
    TensorFlow Serving Savant Model Serving FilterBase: tensorflow-serving-filterbase
    TensorFlow Serving Savant Model Serving ExceptionFilterBase: tensorflow-serving- exceptionfilterbase
`
