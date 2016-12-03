package lila.pool

import org.joda.time.DateTime

import lila.rating.RatingRange
import lila.user.User

case class PoolMember(
    userId: User.ID,
    socketId: lila.socket.Socket.Uid,
    rating: Int,
    ratingRange: Option[RatingRange],
    engine: Boolean,
    since: DateTime,
    misses: Int = 0 // how many waves they missed
) {

  def incMisses = copy(misses = misses + 1)

  def waitMillis: Int = (DateTime.now.getMillis - since.getMillis).toInt

  def ratingDiff(other: PoolMember) = Math.abs(rating - other.rating)

  def withRange(r: Option[RatingRange]) =
    if (r == ratingRange) this
    else copy(ratingRange = r, misses = 0)

  def hasRange = ratingRange.isDefined
}

object PoolMember {

  def apply(joiner: PoolApi.Joiner, config: PoolConfig): PoolMember =
    PoolMember(
      userId = joiner.userId,
      socketId = joiner.socketId,
      engine = joiner.engine,
      rating = joiner.ratingMap.getOrElse(config.perfType.key, 1500),
      ratingRange = joiner.ratingRange,
      since = DateTime.now)
}
