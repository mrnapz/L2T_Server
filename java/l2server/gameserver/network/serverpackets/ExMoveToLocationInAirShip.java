/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package l2server.gameserver.network.serverpackets;

import l2server.gameserver.model.actor.instance.L2PcInstance;
import l2server.util.Point3D;

public class ExMoveToLocationInAirShip extends L2GameServerPacket
{
	private int charObjId;
	private int airShipId;
	private Point3D destination;
	private int heading;

	/**
	 */
	public ExMoveToLocationInAirShip(L2PcInstance player)
	{
		this.charObjId = player.getObjectId();
		this.airShipId = player.getAirShip().getObjectId();
		this.destination = player.getInVehiclePosition();
		this.heading = player.getHeading();
	}

	/* (non-Javadoc)
	 * @see l2server.gameserver.serverpackets.ServerBasePacket#writeImpl()
	 */
	@Override
	protected final void writeImpl()
	{
		writeD(this.charObjId);
		writeD(this.airShipId);
		writeD(this.destination.getX());
		writeD(this.destination.getY());
		writeD(this.destination.getZ());
		writeD(this.heading);
	}
}
