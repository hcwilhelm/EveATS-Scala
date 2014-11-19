package models.evedump

import models.core.TypesafeID._

case class ItemID(untypedID: Long) extends AnyVal with TypedID
object ItemID extends TypedIDCompanion[ItemID]