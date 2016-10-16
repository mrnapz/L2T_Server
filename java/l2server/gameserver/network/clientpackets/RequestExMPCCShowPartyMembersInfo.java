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

import l2server.gameserver.model.L2World;
import l2server.gameserver.model.actor.instance.L2PcInstance;
import l2server.gameserver.network.serverpackets.ExMPCCShowPartyMemberInfo;

/**
 * Format:(ch) d
 *
 * @author chris_00
 */
public final class RequestExMPCCShowPartyMembersInfo extends L2GameClientPacket
{
	private int partyLeaderId;

	@Override
	protected void readImpl()
	{
		this.partyLeaderId = readD();
	}

	/**
	 */
	@Override
	protected void runImpl()
	{
		L2PcInstance player = L2World.getInstance().getPlayer(this.partyLeaderId);
		if (player != null && player.getParty() != null)
		{
			getClient().getActiveChar().sendPacket(new ExMPCCShowPartyMemberInfo(player.getParty()));
		}
	}
}
