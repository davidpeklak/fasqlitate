package fasqlitate

import java.sql.{PreparedStatement, Connection}

object ConnectionSyntax {

  implicit class ConnectionOps(val connection: Connection) extends AnyVal {
    def prepareStatement(sp: StatementPreparation): PreparedStatement = {
      import sp._
      val preparedStatement = connection.prepareStatement(string)
      effect(preparedStatement)
      preparedStatement
    }
  }
}
