import java.sql.{Connection, PreparedStatement}

package object fasqlitate {

  case class StatementPreparation(string: String, effect: PreparedStatement => Unit) {
    override def toString: String = s"""StatementPreparation("$string", $effect)"""
  }

  implicit class ConnectionOps(val connection: Connection) extends AnyVal {
    def prepareStatement(sp: StatementPreparation): PreparedStatement = {
      import sp._
      val preparedStatement = connection.prepareStatement(string)
      effect(preparedStatement)
      preparedStatement
    }
  }

  trait TypeSetter {
    def apply(arg: Any, pos: Int): PreparedStatement => Unit
  }

  trait OptTypeSetter {
    def apply(arg: Any, pos: Int): Option[PreparedStatement => Unit]
  }

  def describedFun(f: PreparedStatement => Unit, desc: String) = new Function1[PreparedStatement, Unit] {
    def apply(ps: PreparedStatement): Unit = f(ps)

    override def toString(): String = desc
  }

  object OptIntSetter extends OptTypeSetter {
    def apply(arg: Any, pos: Int): Option[(PreparedStatement) => Unit] = arg match {
      case i: Int => Some(describedFun(_.setInt(pos, i), s"_.setInt($pos, $i)"))
      case _ => None
    }
  }

  object OptStringSetter extends OptTypeSetter {
    def apply(arg: Any, pos: Int): Option[(PreparedStatement) => Unit] = arg match {
      case s: String => Some(describedFun(_.setString(pos, s), s"_.setString($pos, $s)"))
      case _ => None
    }
  }

  class TypeSetterImpl(optTypeSetters: List[OptTypeSetter]
                       = List(OptIntSetter, OptStringSetter))
    extends TypeSetter {

    def apply(arg: Any, pos: Int): PreparedStatement => Unit = {

      optTypeSetters
        .toStream
        .flatMap(ots => ots(arg, pos))
        .headOption match {
        case Some(setter) => setter
        case None => throw new Exception(s"No OptTypeSetter found for type ${arg.getClass.getName}")
      }
    }
  }

  implicit val tsi = new TypeSetterImpl


  implicit class SqlHelper(val stringContext: StringContext) extends AnyVal {
    def sql(args: Any*)(implicit typeSetter: TypeSetter): StatementPreparation = {
      val string = stringContext.parts.mkString(" ? ")

      val effects = for ((arg, pos) <- args.zipWithIndex) yield typeSetter(arg, pos + 1)

      val effect = new Function1[PreparedStatement, Unit] {
        def apply(ps: PreparedStatement): Unit = effects.foreach(_ (ps))

        override def toString(): String = effects.mkString(", ")
      }

      StatementPreparation(string, effect)
    }
  }

}
