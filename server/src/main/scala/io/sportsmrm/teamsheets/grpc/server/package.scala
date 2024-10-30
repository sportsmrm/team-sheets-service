package io.sportsmrm.teamsheets.grpc

import org.apache.pekko.cluster.sharding.typed.scaladsl.EntityRef

package object server {
  type CorrelatorLocator = String => EntityRef[Correlator.Command]
}
