package fasqlitate

import java.sql.PreparedStatement

object Impl {

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

  class TypeSetterImpl(optTypeSetters: List[OptTypeSetter]) extends TypeSetter {

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

}
