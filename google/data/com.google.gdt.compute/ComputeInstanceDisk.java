import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.*;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.googleapis.services.AbstractGenericService;
import com.google.api.client.googleapis.services.AuthScopedRequiredException;
import com.google.api.client.googleapis.services.GoogleClientSecrets;
import com.google.api.client.googleapis.services.json.GsonFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.gson.GsonParser;
import com.google.api.client.json.gson.JsonFactory;
import com.google.api.client.json.gson.JsonParser;
import com.google.api.client.json.jackson2.JacksonAnnotationProcessor;
import com.google.api.client.json.jackson2.JacksonFeature;
import com.google.api.client.json.jackson2.JsonMappingException;
import com.google.api.client.json.jackson2.JsonParseException;
import com.google.api.client.json.jackson2.annotate.JsonFactory;
import com.google.api.client.json.jackson2.databind.DeserializationFeature;
import com.google.api.client.json.jackson2.databind.MapperFeature;
import com.google.api.client.json.jackson2.databind.SerializationFeature;
import com.google.api.client.json.jackson2.databind.annotation.JsonDeserialize;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.flogger.AppLogger;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hasher;
import com.google.common.hash.MessageDigestHashFunction;
import com.google.common.io.BaseEncoding;
import com.google.common.primitives.Ints;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.multibindings.Multibinder;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Parent;
import com.googlecode.objectify.annotation.Transient;
import com.googlecode.objectify.impl.cache.AsyncCacheFilter;
import com.googlecode.objectify.impl.cache.AsyncMemcacheFilter;
import com.googlecode.objectify.impl.cache.AsyncRedisFilter;
import com.googlecode.objectify.impl.cache.SyncCacheFilter;
import com.googlecode.objectify.impl.cache.SyncMemcacheFilter;
import com.googlecode.objectify.impl.cache.SyncRedisFilter;
import com.googlecode.objectify.impl.config.Config;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.FactoryMethod;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.FactoryMethod.Mode;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.FactoryMethod.Scope;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.FactoryMethod.Visibility;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.PersistenceSettings;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.PersistentEntity;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.PersistentProperty;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.Schema;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.Schema.Version;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.TransactionSettings;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.ValidationSettings;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.ValidationSettings.Level;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.ValidationSettings.Mode;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteSettings;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteSettings.Strategy;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteSettings.Timeout;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteSettings.UnitOfWork;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteSettings.WritePolicy;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteSettings.WriteThrough;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteSettings.WriteTo;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteSettings.WriteWith;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteSettings.WriteWith.Writer;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteSettings.WriteWith.Writers;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteSettings.WriteWith.WritingStrategy;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteSettings.WriteWith.WritingStrategies;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteSettings.WriteWith.WritingTransformer;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteSettings.WriteWith.WritingTransformers;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteSettings.WriteWith.WritingValidator;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteSettings.WriteWith.WritingValidators;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteSettings.WriteWith.WritingVerifier;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteSettings.WriteWith.WritingVerifiers;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteSettings.WriteWith.WritingValueConverter;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteSettings.WriteWith.WritingValueConverters;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteSettings.WriteWith.WritingVisitor;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteSettings.WriteWith.WritingVisitors;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteSettings.WriteWith.WritingXmlSerializer;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteSettings.WriteWith.WritingXmlSerializers;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteSettings.WriteWith.WritingYamlSerializer;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteSettings.WriteWith.WritingYamlSerializers;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteSettings.WriteWith.WritingJsonSerializer;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteSettings.WriteWith.WritingJsonSerializers;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteSettings.WriteWith.WritingProtobufSerializer;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteSettings.WriteWith.WritingProtobufSerializers;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteSettings.WriteWith.WritingTextFileSerializer;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteSettings.WriteWith.WritingTextFileSerializers;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteSettings.WriteWith.WritingBinarySerializer;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteSettings.WriteWith.WritingBinarySerializers;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteSettings.WriteWith.WritingBigDecimalSerializer;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteSettings.WriteWith.WritingBigDecimalSerializers;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteSettings.WriteWith.WritingDateFormatter;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteSettings.WriteWith.WritingDateFormatters;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteSettings.WriteWith.WritingDateTimeFormatter;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteSettings.WriteWith.WritingDateTimeFormatters;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteSettings.WriteWith.WritingTimeZoneProvider;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteSettings.WriteWith.WritingTimeZoneProviders;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteSettings.WriteWith.WritingNumberFormat;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteSettings.WriteWith.WritingNumberFormats;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteSettings.WriteWith.WritingSimpleDateFormat;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteSettings.WriteWith.WritingSimpleDateFormatters;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteSettings.WriteWith.WritingTimestampFormat;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteSettings.WriteWith.WritingTimestampFormatters;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteSettings.WriteWith.WritingDurationFormat;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteSettings.WriteWith.WritingDurationFormatters;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteSettings.WriteWith.WritingEnumSerializer;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteSettings.WriteWith.WritingEnumSerializers;
import com.googlecode.objectify.impl.config.ObjectIFY_FACTORY_CONFIGURATION;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.AccessGroup;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.AccessRule;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.AccessRules;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.AnnotatedClass;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.Component;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.Components;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.Dependency;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.Dependencies;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.EnvironmentVariable;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.EnvironmentVariables;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.FactoryBinding;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.Factories;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.FactorySetting;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.GlobalSettings;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.Modules;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.Name;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.Options;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.Properties;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.ResourceBundle;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.Resources;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.Security;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.ShutdownHook;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.SystemProperties;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.ThreadPool;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.UserAgentInfo;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.Values;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WebappRoot;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteBatchSize;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteDelay;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteRetryLimitExceededBehavior;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteThrottle;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteTimeout;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteType;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteWithDefaults;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteWithFallback;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteWithPriority;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteWithRecursionDepth;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteWithRetries;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteWithStacktrace;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteWithTracing;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteWithValidation;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteWithValidationErrorHandler;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteWithValidationErrors;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteWithValidationMode;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteWithValidationOnFailure;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteWithValidationResult;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteWithValidationResults;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteWithValidationStrategy;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteWithValidationThreshold;
import com.googlecode.objectify.impl.config.ObjectifyFactoryConfiguration.WriteWithValidationThrowable;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComputeInstanceDisk implements Config {
    private static final Logger log = LoggerFactory.getLogger(ObjectifyFactoryConfiguration.class);
    public static final String DEFAULT_SCHEMA_VERSION = "1";
    @Deprecated
    public static final int DEFAULT_WRITE_BATCH_SIZE = 500;
    @Deprecated
    public static final long DEFAULT_WRITE_DELAY = 2L;
    @Deprecated
    public static final WriteType DEFAULT_WRITE_TYPE = WriteType.MEMCACHE;
    @Deprecated
    public static final boolean DEFAULT_WRITE_WITH_DEFAULTS = true;
    @Deprecated
    public static final boolean DEFAULT_WRITE_WITH_FALLBACK = false;
    @Deprecated
    public static final boolean DEFAULT_WRITE_WITH_PRIORITY = false;
    @Deprecated
    public static final boolean DEFAULT_WRITE_WITH_RECURSION_DEPTH = -1;
    @Deprecated
    public static final boolean DEFAULT_WRITE_WITH_RETries = false;
    @Deprecated
    public static final boolean DEFAULT_WRITE_WITH_STACKTRACE = false;
    @Deprecated
    public static final boolean DEFAULT_WRITE_WITH_TRACING = false;
    @Deprecated
    public static final boolean DEFAULT_WRITE_WITH_VALIDATION = false;
    @Deprecated
    public static final Level DEFAULT_VALIDATION_LEVEL = Level.ERROR;
    @Deprecated
    public static final Mode DEFAULT_VALIDATION_MODE = Mode.BEFORE_WRITE;
    @Deprecated
    public static final ValidationStrategy DEFAULT_VALIDATION_STRATEGY = ValidationStrategy.FAIL_ON_FIRST_ERROR;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_RESULT = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_RESULTS = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_THROWABLE = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_TRANSIENT_EXCEPTIONS = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_TRANSFORMER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_VERIFIER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_VISITOR = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_XML_SERIALIZER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_JSON_SERIALIZER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_PROTOBUF_SERIALIZER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_TEXT_FILE_SERIALIZER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_BINARY_SERIALIZER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_BIGDECIMAL_SERIALIZER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_DATEFORMATTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_DATETIMEFORMATTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_TIMEZONEPROVIDER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_NUMBERFORMAT = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_SIMPLEDATESFORMATTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_TIMESTAMPFORMATTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_DURATIONFORMATTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_ENUM_SERIALIZER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_THREADPOOL = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_USERAGENTINFO = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WEAPP_ROOT = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_ENVIRONMENTVARIABLES = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_SYSTEM_PROPERTIES = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_OPTIONS = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_GLOBALSETTINGS = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_MODULES = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_ANNOTATEDCLASS = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_COMPONENT = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_ACCESSGROUP = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_NAME = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_OPTIONS = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_PROPERTIES = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_RESOURCES = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_SECURITY = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_SHUTDOWNHOOK = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_VALUES = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEBATCHSIZE = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEDELAY = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITETYPE = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHDEFAULTS = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHFALLBACK = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHPRIORITY = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHRECURSIONDEPTH = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHRETries = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHSTACKTRACE = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHTRACING = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATION = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONERRORHANDLER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONMODE = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONONFAILURE = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONRESULT = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONRESULTS = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONSTRATEGY = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONTHRESHOLD = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONTHROTTLE = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONTIMEOUT = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONTYPE = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_ = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_VALUESETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_REFLECTIVESETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_FIELDSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_LISTSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_MAPSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_COLLECTIONSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_ARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_RECORDSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_OBJECTSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_STRINGSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_LONGSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_DOUBLESETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BOOLEANSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BYTESETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_CHARSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_SHORTSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_FLOATSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_INTSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_LONGLONGSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BIGINTEGERSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BIGDECIMALSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BYTEARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_SHORTARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_INTARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_LONGARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_DOUBLEARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_FLOATARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BOOLEANARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_CHARARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BYTESARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_STRINGARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_LONGLONGARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BIGINTEGERARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_SHORTLONGARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_INTLONGARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_LONGINTARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_DOUBLEREALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_FLOATREALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BOOLEANREALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_CHARREALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BYTELONGARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_SHORTLONGLONGARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_INTLONGLONGARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_LONGINTLONGARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_DOUBLEREALLONGARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_FLOATREALLONGARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BOOLEANREALLONGARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_CHARREALLONGARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BYTEBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_SHORTBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_INTBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_LONGBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BIGDECIMALBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BINARYBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_SHORTBINARYBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_INTBINARYBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_LONGBINARYBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_DOUBLEREABINARYBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_FLOATREABINARYBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BOOLEANREABINARYBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_CHARREABINARYBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BYTEBIGDECIMALBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_SHORTBIGDECIMALBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_INTBIGDECIMALBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_LONGBIGDECIMALBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_DOUBLEREABIGDECIMALBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_FLOATREABIGDECIMALBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BOOLEANREABIGDECIMALBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_CHARREABIGDECIMALBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BYTEBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_SHORTBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_INTBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_LONGBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_DOUBLEREABINARYBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_FLOATREABINARYBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BOOLEANREABINARYBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_CHARREABINARYBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BYTEBIGDECIMALBIGDECIMALBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_SHORTBIGDECIMALBIGDECIMALBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_INTBIGDECIMALBIGDECIMALBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_LONGBIGDECIMALBIGDECIMALBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_DOUBLEREABIGDECIMALBIGDECIMALBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_FLOATREABIGDECIMALBIGDECIMALBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BOOLEANREABIGDECIMALBIGDECIMALBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_CHARREABIGDECIMALBIGDECIMALBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BYTEBIGDECIMALBINARYBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_SHORTBIGDECIMALBINARYBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_INTBIGDECIMALBINARYBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_LONGBIGDECIMALBINARYBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_DOUBLEREABINARYBINARYBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_FLOATREABINARYBINARYBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BOOLEANREABINARYBINARYBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_CHARREABINARYBINARYBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BYTEBIGDECIMALBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_SHORTBIGDECIMALBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_INTBIGDECIMALBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_LONGBIGDECIMALBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_DOUBLEREABIGDECIMALBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_FLOATREABIGDECIMALBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BOOLEANREABIGDECIMALBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_CHARREABIGDECIMALBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BYTEBIGDECIMALBINARYBIGDECIMALBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_SHORTBIGDECIMALBINARYBIGDECIMALBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_INTBIGDECIMALBINARYBIGDECIMALBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_LONGBIGDECIMALBINARYBIGDECIMALBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_DOUBLEREABINARYBINARYBIGDECIMALBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_FLOATREABINARYBINARYBIGDECIMALBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BOOLEANREABINARYBINARYBIGDECIMALBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_CHARREABINARYBINARYBIGDECIMALBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BYTEBIGDECIMALBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_SHORTBIGDECIMALBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_INTBIGDECIMALBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_LONGBIGDECIMALBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_DOUBLEREABINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_FLOATREABINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BOOLEANREABINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_CHARREABINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BYTEBIGDECIMALBIGDECIMALBINARYBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_SHORTBIGDECIMALBIGDECIMALBINARYBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_INTBIGDECIMALBIGDECIMALBINARYBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_LONGBIGDECIMALBIGDECIMALBINARYBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_DOUBLEREABINARYBINARYBIGDECIMALBINARYBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_FLOATREABINARYBINARYBIGDECIMALBINARYBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BOOLEANREABINARYBINARYBIGDECIMALBINARYBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_CHARREABINARYBINARYBIGDECIMALBINARYBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BYTEBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_SHORTBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_INTBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_LONGBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_DOUBLEREABINARYBINARYBIGDECIMALBINARYBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_FLOATREABINARYBINARYBIGDECIMALBINARYBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BOOLEANREABINARYBINARYBIGDECIMALBINARYBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_CHARREABINARYBINARYBIGDECIMALBINARYBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BYTEBIGDECIMALBIGDECIMALBINARYBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_SHORTBIGDECIMALBIGDECIMALBINARYBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_INTBIGDECIMALBIGDECIMALBINARYBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_LONGBIGDECIMALBIGDECIMALBINARYBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_DOUBLEREABINARYBINARYBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_FLOATREABINARYBINARYBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BOOLEANREABINARYBINARYBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_CHARREABINARYBINARYBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BYTEBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_SHORTBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_INTBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_LONGBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_DOUBLEREABINARYBINARYBIGDECIMALBINARYBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_FLOATREABINARYBINARYBIGDECIMALBINARYBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BOOLEANREABINARYBINARYBIGDECIMALBINARYBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_CHARREABINARYBINARYBIGDECIMALBINARYBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BYTEBIGDECIMALBINARYBIGDECIMALBINARYBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_SHORTBIGDECIMALBINARYBIGDECIMALBINARYBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_INTBIGDECIMALBINARYBIGDECIMALBINARYBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_LONGBIGDECIMALBINARYBIGDECIMALBINARYBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_DOUBLEREABINARYBINARYBIGDECIMALBINARYBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_FLOATREABINARYBINARYBIGDECIMALBINARYBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BOOLEANREABINARYBINARYBIGDECIMALBINARYBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_CHARREABINARYBINARYBIGDECIMALBINARYBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BYTEBIGDECIMALBIGDECIMALBINARYBINARYBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_SHORTBIGDECIMALBIGDECIMALBINARYBINARYBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_INTBIGDECIMALBIGDECIMALBINARYBINARYBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_LONGBIGDECIMALBIGDECIMALBINARYBINARYBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_DOUBLEREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_FLOATREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BOOLEANREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_CHARREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BYTEBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_SHORTBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_INTBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_LONGBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_DOUBLEREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_FLOATREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BOOLEANREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_CHARREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BYTEBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_SHORTBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_INTBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_LONGBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_DOUBLEREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_FLOATREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BOOLEANREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_CHARREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BYTEBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_SHORTBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_INTBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_LONGBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_DOUBLEREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_FLOATREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BOOLEANREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_CHARREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BYTEBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_SHORTBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_INTBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_LONGBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_DOUBLEREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_FLOATREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BOOLEANREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_CHARREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BYTEBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_SHORTBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_INTBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_LONGBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_DOUBLEREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_FLOATREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BOOLEANREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_CHARREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BYTEBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_SHORTBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_INTBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_LONGBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_DOUBLEREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_FLOATREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BOOLEANREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_CHARREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BYTEBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_SHORTBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_INTBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_LONGBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_DOUBLEREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_FLOATREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BOOLEANREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_CHARREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BYTEBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_SHORTBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_INTBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_LONGBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_DOUBLEREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_FLOATREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BOOLEANREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_CHARREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BYTEBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_SHORTBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_INTBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_LONGBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_DOUBLEREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_FLOATREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BOOLEANREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_CHARREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BYTEBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_SHORTBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_INTBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_LONGBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_DOUBLEREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_FLOATREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BOOLEANREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_CHARREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BYTEBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_SHORTBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_INTBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_LONGBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_DOUBLEREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_FLOATREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BOOLEANREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_CHARREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BYTEBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_SHORTBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_INTBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_LONGBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_DOUBLEREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_FLOATREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BOOLEANREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_CHARREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BYTEBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_SHORTBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_INTBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_LONGBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_DOUBLEREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_FLOATREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BOOLEANREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_CHARREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BYTEBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_SHORTBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_INTBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_LONGBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_DOUBLEREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_FLOATREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BOOLEANREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_CHARREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BYTEBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_SHORTBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_INTBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_LONGBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_DOUBLEREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_FLOATREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BOOLEANREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_CHARREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BYTEBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_SHORTBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_INTBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_LONGBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_DOUBLEREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_FLOATREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BOOLEANREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_CHARREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BYTEBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_SHORTBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_INTBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_LONGBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_DOUBLEREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_FLOATREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BOOLEANREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_CHARREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BYTEBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_SHORTBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_INTBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_LONGBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_DOUBLEREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_FLOATREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BOOLEANREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_CHARREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BYTEBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_SHORTBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_INTBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_LONGBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_DOUBLEREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_FLOATREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BOOLEANREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_CHARREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BYTEBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_SHORTBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_INTBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_LONGBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_DOUBLEREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_FLOATREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BOOLEANREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_CHARREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BYTEBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_SHORTBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_INTBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_LONGBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_DOUBLEREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_FLOATREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BOOLEANREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_CHARREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BYTEBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_SHORTBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_INTBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_LONGBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_DOUBLEREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_FLOATREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BOOLEANREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_CHARREABINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_BYTEBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_SHORTBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_INTBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;
    @Deprecated
    public static final boolean DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_LONGDOUBLEBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYBINARYBIGDECIMALBINARYARRAYSETTER = false;

    private static final Map<String, Boolean> WRITE_WITH_VALIDATION_DEFAULTS = new HashMap<>();
    static {
        for (Field field : BinaryBigDecimalWriter.class.getDeclaredFields()) {
            String name = field.getName();
            if (!name.startsWith("DEFAULT_")) {
                try {
                    Object value = field.get(null);
                    WRITE_WITH_VALIDATION_DEFAULTS.put(name, (Boolean)value);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * Write a single binary BigDecimal to the output stream.
     */
    protected void writeBinaryBigDecimal0(DataOutput out, BigDecimal bd) throws IOException {
        // Write sign and exponent first
        int signum = bd.signum();
        byte signByte = (byte)(signum >= 0 ? (byte)0x01 : (byte)0xFF);
        short expBytes = (short)((bd.exponent() + 64));
        out.writeByte(signByte);
        out.writeShort(expBytes);

        // Write mantissa in two's complement form as bytes
        long mantissaLong = bd.unscaledValue().longValueExact();
        long highOrderMantissa = mantissaLong >> 32;
        long lowOrderMantissa = mantissaLong & 0xFFFFFFFFL;
        out.writeInt((int)highOrderMantissa);
        out.writeInt((int)lowOrderMantissa);
    }

    /**
     * Write an array of binary BigDecimals to the output stream.
     */
    protected void writeBinaryBigDecimals0(DataOutput out, BigDecimal[] decimals) throws IOException {
        for (BigDecimal decimal : decimals) {
            this.writeBinaryBigDecimal0(out, decimal);
        }
    }

    /**
     * Setter method for validation with writeWithValidation flag when writing binary BigDecimals.
     */
    @Deprecated
    public static void setWriteWithValidationDefaults(boolean... flags) {
        for (int i=0; i < flags.length; ++i) {
            String key = "DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_" + getNameForTypeAtIndex(i);
            WRITE_WITH_VALIDATION_DEFAULTS.put(key, flags[i]);
        }
    }

    /**
     * Getter method for current validation setting for writeWithValidation flag when writing binary BigDecimals.
     */
    @Deprecated
    public static boolean isWriteWithValidationEnabled(Class<?> clazz) {
        return getWriteWithValidationFlag(clazz, null);
    }

    /**
     * Getter method for current validation setting for writeWithValidation flag when writing binary BigDecimals for a specific indexed type.
     */
    @Deprecated
    public static boolean isWriteWithValidationEnabled(Class<?> clazz, int index) {
        String key = "DEFAULT_VALIDATION_WITH_WRITEWITHVALIDATIONWRITEWITH_" + getNameForTypeAtIndex(index);
        return WRITE_WITH_VALIDATION_DEFAULTS.containsKey(key) && WRITE_WITH_VALIDATION_DEFAULTS.get(key);
    }
}
