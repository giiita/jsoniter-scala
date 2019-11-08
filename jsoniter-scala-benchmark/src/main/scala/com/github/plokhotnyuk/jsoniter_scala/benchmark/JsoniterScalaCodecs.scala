package com.github.plokhotnyuk.jsoniter_scala.benchmark

import java.time._
import java.util.UUID

import com.github.plokhotnyuk.jsoniter_scala.core._
import com.github.plokhotnyuk.jsoniter_scala.benchmark.SuitEnum.SuitEnum
import com.github.plokhotnyuk.jsoniter_scala.macros.JsonCodecMaker.make
import com.github.plokhotnyuk.jsoniter_scala.macros.CodecMakerConfig
import org.typelevel.jawn.ast.{DeferLong, DeferNum, DoubleNum, JArray, JAtom, JFalse, JNull, JNum, JObject, JString, JTrue, JValue, LongNum}

import scala.annotation.switch
import scala.collection.immutable.{BitSet, IntMap, Map, Seq, Set}
import scala.collection.mutable

object JsoniterScalaCodecs {
  val exceptionWithoutDumpConfig: ReaderConfig = ReaderConfig.withAppendHexDumpToParseException(false)
  val exceptionWithStacktraceConfig: ReaderConfig = ReaderConfig.withThrowReaderExceptionWithStackTrace(true)
  val tooLongStringConfig: ReaderConfig = ReaderConfig.withPreferredBufSize(1024 * 1024)
  val escapingConfig: WriterConfig = WriterConfig.withEscapeUnicode(true)
  val prettyConfig: WriterConfig = WriterConfig.withIndentionStep(2).withPreferredBufSize(32768)
  val base16Codec: JsonValueCodec[Array[Byte]] = // don't define implicit for supported types
    new JsonValueCodec[Array[Byte]] {
      override def decodeValue(in: JsonReader, default: Array[Byte]): Array[Byte] = in.readBase16AsBytes(default)

      override def encodeValue(x: Array[Byte], out: JsonWriter): Unit = out.writeBase16Val(x, lowerCase = true)

      override val nullValue: Array[Byte] = new Array[Byte](0)
    }
  val base64Codec: JsonValueCodec[Array[Byte]] = // don't define implicit for supported types
    new JsonValueCodec[Array[Byte]] {
      override def decodeValue(in: JsonReader, default: Array[Byte]): Array[Byte] = in.readBase64AsBytes(default)

      override def encodeValue(x: Array[Byte], out: JsonWriter): Unit = out.writeBase64Val(x, doPadding = true)

      override val nullValue: Array[Byte] = new Array[Byte](0)
    }
  val bigDecimalCodec: JsonValueCodec[BigDecimal] =
    make(CodecMakerConfig.withBigDecimalDigitsLimit(Int.MaxValue).withBigDecimalScaleLimit(Int.MaxValue).withBigDecimalPrecision(0)) /*WARNING: don't do this for open-systems*/
  val bigIntCodec: JsonValueCodec[BigInt] =
    make(CodecMakerConfig.withBigIntDigitsLimit(Int.MaxValue)) // WARNING: don't do this for open-systems
  val intCodec: JsonValueCodec[Int] = make(CodecMakerConfig) // don't define implicit for supported types
  val stringCodec: JsonValueCodec[String] = make(CodecMakerConfig) // don't define implicit for supported types
  implicit val adtCodec: JsonValueCodec[ADTBase] =
    make(CodecMakerConfig.withAllowRecursiveTypes(true)) // WARNING: don't do this for open-systems
  implicit val anyRefsCodec: JsonValueCodec[AnyRefs] = make(CodecMakerConfig)
  implicit val anyValsCodec: JsonValueCodec[AnyVals] = make(CodecMakerConfig)
  implicit val bigDecimalArrayCodec: JsonValueCodec[Array[BigDecimal]] = make(CodecMakerConfig)
  implicit val bigIntArrayCodec: JsonValueCodec[Array[BigInt]] = make(CodecMakerConfig)
  implicit val booleanArrayBufferCodec: JsonValueCodec[mutable.ArrayBuffer[Boolean]] = make(CodecMakerConfig)
  implicit val booleanArrayCodec: JsonValueCodec[Array[Boolean]] = make(CodecMakerConfig)
  implicit val booleanListCodec: JsonValueCodec[List[Boolean]] = make(CodecMakerConfig)
  implicit val booleanVectorCodec: JsonValueCodec[Vector[Boolean]] = make(CodecMakerConfig)
  implicit val byteArrayCodec: JsonValueCodec[Array[Byte]] = make(CodecMakerConfig)
  implicit val charArrayCodec: JsonValueCodec[Array[Char]] = make(CodecMakerConfig)
  implicit val doubleArrayCodec: JsonValueCodec[Array[Double]] = make(CodecMakerConfig)
  implicit val durationArrayCodec: JsonValueCodec[Array[Duration]] = make(CodecMakerConfig)
  implicit val enumArrayCodec: JsonValueCodec[Array[SuitEnum]] = make(CodecMakerConfig)
  implicit val enumADTArrayCodec: JsonValueCodec[Array[SuitADT]] = make(CodecMakerConfig.withDiscriminatorFieldName(None))
  implicit val floatArrayCodec: JsonValueCodec[Array[Float]] = make(CodecMakerConfig)
  implicit val geoJSONCodec: JsonValueCodec[GeoJSON.GeoJSON] = make(CodecMakerConfig)
  implicit val instantArrayCodec: JsonValueCodec[Array[Instant]] = make(CodecMakerConfig)
  implicit val intArrayCodec: JsonValueCodec[Array[Int]] = make(CodecMakerConfig)
  implicit val javaEnumArrayCodec: JsonValueCodec[Array[Suit]] = make(CodecMakerConfig)
  implicit val longArrayCodec: JsonValueCodec[Array[Long]] = make(CodecMakerConfig)
  implicit val localDateArrayCodec: JsonValueCodec[Array[LocalDate]] = make(CodecMakerConfig)
  implicit val localDateTimeArrayCodec: JsonValueCodec[Array[LocalDateTime]] = make(CodecMakerConfig)
  implicit val localTimeArrayCodec: JsonValueCodec[Array[LocalTime]] = make(CodecMakerConfig)
  implicit val monthDayArrayCodec: JsonValueCodec[Array[MonthDay]] = make(CodecMakerConfig)
  implicit val nestedStructsCodec: JsonValueCodec[NestedStructs] =
    make(CodecMakerConfig.withAllowRecursiveTypes(true)) // WARNING: don't do this for open-systems
  implicit val offsetDateTimeArrayCodec: JsonValueCodec[Array[OffsetDateTime]] = make(CodecMakerConfig)
  implicit val offsetTimeArrayCodec: JsonValueCodec[Array[OffsetTime]] = make(CodecMakerConfig)
  implicit val openRTB25Codec: JsonValueCodec[OpenRTB.BidRequest] = make(CodecMakerConfig)
  implicit val periodArrayCodec: JsonValueCodec[Array[Period]] = make(CodecMakerConfig)
  implicit val shortArrayCodec: JsonValueCodec[Array[Short]] = make(CodecMakerConfig)
  implicit val uuidArrayCodec: JsonValueCodec[Array[UUID]] = make(CodecMakerConfig)
  implicit val yearArrayCodec: JsonValueCodec[Array[Year]] = make(CodecMakerConfig)
  implicit val yearMonthArrayCodec: JsonValueCodec[Array[YearMonth]] = make(CodecMakerConfig)
  implicit val zonedDateTimeArrayCodec: JsonValueCodec[Array[ZonedDateTime]] = make(CodecMakerConfig)
  implicit val zoneIdArrayCodec: JsonValueCodec[Array[ZoneId]] = make(CodecMakerConfig)
  implicit val zoneOffsetArrayCodec: JsonValueCodec[Array[ZoneOffset]] = make(CodecMakerConfig)
  implicit val bitSetCodec: JsonValueCodec[BitSet] =
    make(CodecMakerConfig.withBitSetValueLimit(Int.MaxValue)) // WARNING: don't do this for open-systems
  implicit val extractFieldsCodec: JsonValueCodec[ExtractFields] = make(CodecMakerConfig)
  implicit val intMapOfBooleansCodec: JsonValueCodec[IntMap[Boolean]] =
    make(CodecMakerConfig.withMapMaxInsertNumber(Int.MaxValue)) // WARNING: don't do this for open-systems
  implicit val googleMapsAPICodec: JsonValueCodec[GoogleMapsAPI.DistanceMatrix] = make(CodecMakerConfig)
  implicit val mapOfIntsToBooleansCodec: JsonValueCodec[Map[Int, Boolean]] =
    make(CodecMakerConfig.withMapMaxInsertNumber(Int.MaxValue)) // WARNING: don't do this for open-systems
  implicit val missingReqFieldCodec: JsonValueCodec[MissingRequiredFields] = make(CodecMakerConfig)
  implicit val mutableBitSetCodec: JsonValueCodec[mutable.BitSet] =
    make(CodecMakerConfig.withBitSetValueLimit(Int.MaxValue /*WARNING: don't do this for open-systems*/))
  implicit val mutableLongMapOfBooleansCodec: JsonValueCodec[mutable.LongMap[Boolean]] =
    make(CodecMakerConfig.withMapMaxInsertNumber(Int.MaxValue)) // WARNING: don't do this for open-systems
  implicit val mutableMapOfIntsToBooleansCodec: JsonValueCodec[mutable.Map[Int, Boolean]] =
    make(CodecMakerConfig.withMapMaxInsertNumber(Int.MaxValue)) // WARNING: don't do this for open-systems
  implicit val mutableSetOfIntsCodec: JsonValueCodec[mutable.Set[Int]] =
    make(CodecMakerConfig.withSetMaxInsertNumber(Int.MaxValue)) // WARNING: don't do this for open-systems
  implicit val primitivesCodec: JsonValueCodec[Primitives] = make(CodecMakerConfig)
  implicit val setOfIntsCodec: JsonValueCodec[Set[Int]] = make(CodecMakerConfig)
  implicit val twitterAPICodec: JsonValueCodec[Seq[TwitterAPI.Tweet]] = make(CodecMakerConfig)
  implicit val jawnASTCodec: JsonValueCodec[JValue] = new JsonValueCodec[JValue] {
    import scala.jdk.CollectionConverters._

    override def decodeValue(in: JsonReader, default: JValue): JValue = {
      var b = in.nextToken()
      if (b =='"') {
        in.rollbackToken()
        new JString(in.readString(null))
      } else if (b == 'f' || b == 't') {
        in.rollbackToken()
        if (in.readBoolean()) JTrue
        else JFalse
      } else if (b == 'n') {
        in.readNullOrError(default, "expected `null` value")
        JNull
      } else if ((b >= '0' && b <= '9') || b == '-') {
        in.rollbackToken()
        in.setMark()
        val bs = in.readRawValAsBytes()
        val l = bs.length
        var i = 0
        while (i < l && {
          b = bs(i)
          b >= '0' && b <= '9'
        }) i += 1
        in.rollbackToMark()
        if (i == l) new LongNum(in.readLong())
        else new DoubleNum(in.readDouble())
      } else if (b == '{') {
        val obj = new java.util.LinkedHashMap[String, JValue]
        if (!in.isNextToken('}')) {
          in.rollbackToken()
          do obj.put(in.readKeyAsString(), decodeValue(in, default))
          while (in.isNextToken(','))
          if (!in.isCurrentToken('}')) in.objectEndOrCommaError()
        }
        new JObject(obj.asScala)
      } else if (b == '[') {
        val arr = new mutable.ArrayBuffer[JValue]
        if (!in.isNextToken(']')) {
          in.rollbackToken()
          do arr += decodeValue(in, default)
          while (in.isNextToken(','))
          if (!in.isCurrentToken(']')) in.arrayEndOrCommaError()
        }
        new JArray(arr.toArray)
      } else in.decodeError("expected JSON value")
    }

    override def encodeValue(x: JValue, out: JsonWriter): Unit = x match {
      case _: JAtom => x match {
        case JString(str) => out.writeVal(str)
        case JTrue => out.writeVal(true)
        case JFalse => out.writeVal(false)
        case JNull => out.writeNull()
        case _: JNum => x match {
          case LongNum(n) => out.writeVal(n)
          case DoubleNum(d) => out.writeVal(d)
          case DeferLong(str) => out.writeRawVal(str.getBytes)
          case DeferNum(str) => out.writeRawVal(str.getBytes)
        }
      }
      case JObject(obj) =>
        out.writeObjectStart()
        obj.foreach { case (k, v) =>
          out.writeKey(k)
          encodeValue(v, out)
        }
        out.writeObjectEnd()
      case JArray(arr) =>
        out.writeArrayStart()
        arr.foreach(v => encodeValue(v, out))
        out.writeArrayEnd()
    }

    override val nullValue: JValue = JNull
  }
}
