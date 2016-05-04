package fasqlitate

import java.sql.PreparedStatement
import java.sql.Types._

object Opt {

  trait SqlTypeProvider[T] {
    val sqlType: Int
    val sqlTypeDesc: String
  }

  implicit val StringSqlTypeProvider = new SqlTypeProvider[String] {
    val sqlType = VARCHAR
    val sqlTypeDesc = "VARCHAR"
  }

  implicit val IntSqlTypeProvider = new SqlTypeProvider[Int] {
    val sqlType = INTEGER
    val sqlTypeDesc = "INTEGER"
  }

  case class Opto[T](opt: Option[T], sqlTypeProvider: SqlTypeProvider[T])

  def o[T: SqlTypeProvider](opt: Option[T]): Opto[T] = Opto(opt, implicitly[SqlTypeProvider[T]])

  class OptOptSetter(typeSetter: TypeSetter) extends Impl.OptTypeSetter {
    def apply(arg: Any, pos: Int): Option[(PreparedStatement) => Unit] = arg match {
      case Opto(Some(t), _) => Some(typeSetter(t, pos))
      case Opto(None, p) => Some(Impl.describedFun(_.setNull(pos, p.sqlType), s"._setNull($pos, ${p.sqlTypeDesc}"))
      case _ => None
    }
  }

  object Default {
    val optTypeSetters = fasqlitate.Default.defaultOptTypeSetters :+ new OptOptSetter(fasqlitate.Default.tsi)

    implicit val tsi = new Impl.TypeSetterImpl(optTypeSetters)
  }

}
