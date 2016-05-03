package fasqlitate

object Default {

  import Impl._

  val defaultOptTypeSetters: List[OptTypeSetter] = List(OptIntSetter, OptStringSetter)

  implicit val tsi = new TypeSetterImpl(defaultOptTypeSetters)
}
