/*
 * This file is part of Applied Energistics 2.
 * Copyright (c) 2013 - 2014, AlgorithmX2, All rights reserved.
 *
 * Applied Energistics 2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Applied Energistics 2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Applied Energistics 2.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */

package appeng.util.inv;


import java.util.Iterator;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import appeng.api.config.FuzzyMode;
import appeng.util.InventoryAdaptor;
import appeng.util.Platform;


public class AdaptorIInventory extends InventoryAdaptor
{

	private final IInventory i;
	private final boolean wrapperEnabled;

	public AdaptorIInventory( final IInventory s )
	{
		this.i = s;
		this.wrapperEnabled = s instanceof IInventoryWrapper;
	}

	@Override
	public ItemStack removeItems( int amount, ItemStack filter, final IInventoryDestination destination )
	{
		final int s = this.i.getSizeInventory();
		ItemStack rv = ItemStack.EMPTY;

		for( int x = 0; x < s && amount > 0; x++ )
		{
			final ItemStack is = this.i.getStackInSlot( x );
			if( !is.isEmpty() && this.canRemoveStackFromSlot( x, is ) && ( filter.isEmpty() || Platform.itemComparisons().isSameItem( is, filter ) ) )
			{
				int boundAmounts = amount;
				if( boundAmounts > is.getCount() )
				{
					boundAmounts = is.getCount();
				}
				if( destination != null && !destination.canInsert( is ) )
				{
					boundAmounts = 0;
				}

				if( boundAmounts > 0 )
				{
					if( rv.isEmpty() )
					{
						rv = is.copy();
						filter = rv;
						rv.setCount( boundAmounts );
						amount -= boundAmounts;
					}
					else
					{
						rv.grow( boundAmounts );
						amount -= boundAmounts;
					}

					if( is.getCount() == boundAmounts )
					{
						this.i.setInventorySlotContents( x, ItemStack.EMPTY );
						this.i.markDirty();
					}
					else
					{
						final ItemStack po = is.copy();
						po.grow( -boundAmounts );
						this.i.setInventorySlotContents( x, po );
						this.i.markDirty();
					}
				}
			}
		}

		// if ( rv != null )
		// i.markDirty();

		return rv;
	}

	@Override
	public ItemStack simulateRemove( int amount, final ItemStack filter, final IInventoryDestination destination )
	{
		final int s = this.i.getSizeInventory();
		ItemStack rv = ItemStack.EMPTY;

		for( int x = 0; x < s && amount > 0; x++ )
		{
			final ItemStack is = this.i.getStackInSlot( x );
			if( !is.isEmpty() && this.canRemoveStackFromSlot( x, is ) && ( filter.isEmpty() || Platform.itemComparisons().isSameItem( is, filter ) ) )
			{
				int boundAmount = amount;
				if( boundAmount > is.getCount() )
				{
					boundAmount = is.getCount();
				}
				if( destination != null && !destination.canInsert( is ) )
				{
					boundAmount = 0;
				}

				if( boundAmount > 0 )
				{
					if( rv.isEmpty() )
					{
						rv = is.copy();
						rv.setCount( boundAmount );
						amount -= boundAmount;
					}
					else
					{
						rv.grow( boundAmount );
						amount -= boundAmount;
					}
				}
			}
		}

		return rv;
	}

	@Override
	public ItemStack removeSimilarItems( final int amount, final ItemStack filter, final FuzzyMode fuzzyMode, final IInventoryDestination destination )
	{
		final int s = this.i.getSizeInventory();
		for( int x = 0; x < s; x++ )
		{
			final ItemStack is = this.i.getStackInSlot( x );
			if( !is.isEmpty() && this.canRemoveStackFromSlot( x,
					is ) && ( filter.isEmpty() || Platform.itemComparisons().isFuzzyEqualItem( is, filter, fuzzyMode ) ) )
			{
				int newAmount = amount;
				if( newAmount > is.getCount() )
				{
					newAmount = is.getCount();
				}
				if( destination != null && !destination.canInsert( is ) )
				{
					newAmount = 0;
				}

				ItemStack rv = ItemStack.EMPTY;
				if( newAmount > 0 )
				{
					rv = is.copy();
					rv.setCount( newAmount );

					if( is.getCount() == rv.getCount() )
					{
						this.i.setInventorySlotContents( x, ItemStack.EMPTY );
						this.i.markDirty();
					}
					else
					{
						final ItemStack po = is.copy();
						po.grow( -rv.getCount() );
						this.i.setInventorySlotContents( x, po );
						this.i.markDirty();
					}
				}

				if( !rv.isEmpty() )
				{
					// i.markDirty();
					return rv;
				}
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack simulateSimilarRemove( final int amount, final ItemStack filter, final FuzzyMode fuzzyMode, final IInventoryDestination destination )
	{
		final int s = this.i.getSizeInventory();
		for( int x = 0; x < s; x++ )
		{
			final ItemStack is = this.i.getStackInSlot( x );

			if( !is.isEmpty() && this.canRemoveStackFromSlot( x,
					is ) && ( filter.isEmpty() || Platform.itemComparisons().isFuzzyEqualItem( is, filter, fuzzyMode ) ) )
			{
				int boundAmount = amount;
				if( boundAmount > is.getCount() )
				{
					boundAmount = is.getCount();
				}
				if( destination != null && !destination.canInsert( is ) )
				{
					boundAmount = 0;
				}

				if( boundAmount > 0 )
				{
					final ItemStack rv = is.copy();
					rv.setCount( boundAmount );
					return rv;
				}
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack addItems( final ItemStack toBeAdded )
	{
		return this.addItems( toBeAdded, true );
	}

	@Override
	public ItemStack simulateAdd( final ItemStack toBeSimulated )
	{
		return this.addItems( toBeSimulated, false );
	}

	@Override
	public boolean containsItems()
	{
		final int s = this.i.getSizeInventory();
		for( int x = 0; x < s; x++ )
		{
			if( !this.i.getStackInSlot( x ).isEmpty() )
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Adds an {@link ItemStack} to the adapted {@link IInventory}.
	 *
	 * It respects the inventories stack limit, which can result in not all items added and some left ones are returned.
	 * The ItemStack next is required for inventories, which will fail on isItemValidForSlot() for stacksizes larger
	 * than the limit.
	 *
	 * @param itemsToAdd itemStack to add to the inventory
	 * @param modulate true to modulate, false for simulate
	 *
	 * @return the left itemstack, which could not be added
	 */
	private ItemStack addItems( final ItemStack itemsToAdd, final boolean modulate )
	{
		if( itemsToAdd.isEmpty() || itemsToAdd.getCount() == 0 )
		{
			return ItemStack.EMPTY;
		}

		final ItemStack left = itemsToAdd.copy();
		final int stackLimit = itemsToAdd.getMaxStackSize();
		final int perOperationLimit = Math.min( this.i.getInventoryStackLimit(), stackLimit );
		final int inventorySize = this.i.getSizeInventory();

		for( int slot = 0; slot < inventorySize; slot++ )
		{
			final ItemStack next = left.copy();
			next.setCount( Math.min( perOperationLimit, next.getCount() ) );

			if( this.i.isItemValidForSlot( slot, next ) )
			{
				final ItemStack is = this.i.getStackInSlot( slot );
				if( is.isEmpty() )
				{
					left.grow( -next.getCount() );

					if( modulate )
					{
						this.i.setInventorySlotContents( slot, next );
						this.i.markDirty();
					}

					if( left.getCount() <= 0 )
					{
						return ItemStack.EMPTY;
					}
				}
				else if( Platform.itemComparisons().isSameItem( is, left ) && is.getCount() < perOperationLimit )
				{
					final int room = perOperationLimit - is.getCount();
					final int used = Math.min( left.getCount(), room );

					if( modulate )
					{
						is.grow( used );
						this.i.setInventorySlotContents( slot, is );
						this.i.markDirty();
					}

					left.grow( -used );
					if( left.getCount() <= 0 )
					{
						return ItemStack.EMPTY;
					}
				}
			}
		}

		return left;
	}

	private boolean canRemoveStackFromSlot( final int x, final ItemStack is )
	{
		if( this.wrapperEnabled )
		{
			return ( (IInventoryWrapper) this.i ).canRemoveItemFromSlot( x, is );
		}
		return true;
	}

	@Override
	public Iterator<ItemSlot> iterator()
	{
		return new InvIterator();
	}

	private class InvIterator implements Iterator<ItemSlot>
	{

		private final ItemSlot is = new ItemSlot();
		private int x = 0;

		@Override
		public boolean hasNext()
		{
			return this.x < AdaptorIInventory.this.i.getSizeInventory();
		}

		@Override
		public ItemSlot next()
		{
			final ItemStack iss = AdaptorIInventory.this.i.getStackInSlot( this.x );

			this.is.setExtractable( AdaptorIInventory.this.canRemoveStackFromSlot( this.x, iss ) );
			this.is.setItemStack( iss );

			this.is.setSlot( this.x );
			this.x++;
			return this.is;
		}

		@Override
		public void remove()
		{
			// nothing!
		}
	}
}
