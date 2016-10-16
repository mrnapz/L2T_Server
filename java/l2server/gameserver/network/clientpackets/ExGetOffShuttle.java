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

package l2server.gameserver.network.clientpackets;

import l2server.gameserver.model.L2Object;
import l2server.gameserver.model.L2World;
import l2server.gameserver.model.actor.instance.L2PcInstance;
import l2server.gameserver.model.actor.instance.L2ShuttleInstance;
import l2server.gameserver.network.serverpackets.ActionFailed;

/**
 * @author Pere
 */
public class ExGetOffShuttle extends L2GameClientPacket
{
	private int shuttleId;
	private int posX;
	private int posY;
	private int posZ;

	@Override
	protected void readImpl()
	{
		this.shuttleId = readD();
		this.posX = readD();
		this.posY = readD();
		this.posZ = readD();
	}

	@Override
	protected void runImpl()
	{
		L2PcInstance player = getClient().getActiveChar();
		if (player == null || !player.canGetOnOffShuttle())
		{
			return;
		}

		L2Object obj = L2World.getInstance().findObject(this.shuttleId);
		if (!(obj instanceof L2ShuttleInstance))
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}

		L2ShuttleInstance shuttle = (L2ShuttleInstance) obj;
		if (shuttle.isClosed())
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}

		shuttle.oustPlayer(player, this.posX, this.posY, this.posZ);
	}
}
