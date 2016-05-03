import java.sql.{Connection, PreparedStatement}

package object fasqlitate {

  case class StatementPreparation(string: String, effect: PreparedStatement => Unit) {
    override def toString: String = s"""StatementPreparation("$string", $effect)"""
  }

  trait TypeSetter {
    def apply(arg: Any, pos: Int): PreparedStatement => Unit
  }

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
