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

package l2server.gameserver.stats;

import l2server.gameserver.stats.funcs.Func;

import java.util.ArrayList;

/**
 * A calculator is created to manage and dynamically calculate the effect of a character property (ex : MAX_HP, REGENERATE_HP_RATE...).
 * In fact, each calculator is a table of Func object in which each Func represents a mathematic function : <BR><BR>
 * <p>
 * FuncAtkAccuracy -> Math.sqrt(this.player.getDEX())*6+_player.getLevel()<BR><BR>
 * <p>
 * When the calc method of a calculator is launched, each mathematic function is called according to its priority <B>_order</B>.
 * Indeed, Func with lowest priority order is executed first and Funcs with the same order are executed in unspecified order.
 * The result of the calculation is stored in the value property of an Env class instance.<BR><BR>
 * <p>
 * Method addFunc and removeFunc permit to add and remove a Func object from a Calculator.<BR><BR>
 */

public final class Calculator
{
	/**
	 * Empty Func table definition
	 */
	private static final Func[] emptyFuncs = new Func[0];

	/**
	 * Table of Func object
	 */
	private Func[] functions;

	/**
	 * Constructor of Calculator (Init value : emptyFuncs).<BR><BR>
	 */
	public Calculator()
	{
		this.functions = this.emptyFuncs;
	}

	/**
	 * Constructor of Calculator (Init value : Calculator c).<BR><BR>
	 */
	public Calculator(Calculator c)
	{
		this.functions = c.functions;
	}

	/**
	 * Check if 2 calculators are equals.<BR><BR>
	 */
	public static boolean equalsCals(Calculator c1, Calculator c2)
	{
		if (c1 == c2)
		{
			return true;
		}

		if (c1 == null || c2 == null)
		{
			return false;
		}

		Func[] funcs1 = c1.functions;
		Func[] funcs2 = c2.functions;

		if (funcs1 == funcs2)
		{
			return true;
		}

		if (funcs1.length != funcs2.length)
		{
			return false;
		}

		if (funcs1.length == 0)
		{
			return true;
		}

		for (int i = 0; i < funcs1.length; i++)
		{
			if (funcs1[i] != funcs2[i])
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * Return the number of Funcs in the Calculator.<BR><BR>
	 */
	public int size()
	{
		return this.functions.length;
	}

	/**
	 * Add a Func to the Calculator.<BR><BR>
	 */
	public synchronized void addFunc(Func f)
	{
		Func[] funcs = this.functions;
		Func[] tmp = new Func[funcs.length + 1];

		final int order = f.getOrder();
		int i;

		for (i = 0; i < funcs.length && order >= funcs[i].getOrder(); i++)
		{
			tmp[i] = funcs[i];
		}

		tmp[i] = f;

		for (; i < funcs.length; i++)
		{
			tmp[i + 1] = funcs[i];
		}

		this.functions = tmp;
	}

	/**
	 * Remove a Func from the Calculator.<BR><BR>
	 */
	public synchronized void removeFunc(Func f)
	{
		Func[] funcs = this.functions;
		Func[] tmp = new Func[funcs.length - 1];

		int i;

		for (i = 0; i < funcs.length && f != funcs[i]; i++)
		{
			tmp[i] = funcs[i];
		}

		if (i == funcs.length)
		{
			return;
		}

		for (i++; i < funcs.length; i++)
		{
			tmp[i - 1] = funcs[i];
		}

		if (tmp.length == 0)
		{
			this.functions = this.emptyFuncs;
		}
		else
		{
			this.functions = tmp;
		}
	}

	/**
	 * Remove each Func with the specified owner of the Calculator.<BR><BR>
	 */
	public synchronized ArrayList<Stats> removeOwner(Object owner)
	{
		ArrayList<Stats> modifiedStats = new ArrayList<>();

		for (Func func : this.functions)
		{
			if (func.funcOwner == owner)
			{
				modifiedStats.add(func.stat);
				removeFunc(func);
			}
		}
		return modifiedStats;
	}

	/**
	 * Run each Func of the Calculator.<BR><BR>
	 */
	public void calc(Env env)
	{
		for (Func func : this.functions)
		{
			func.calc(env);
		}
	}
}
