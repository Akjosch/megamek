/**
 * MegaMek - Copyright (C) 2000-2002 Ben Mazur (bmazur@sev.org)
 * 
 *  This program is free software; you can redistribute it and/or modify it 
 *  under the terms of the GNU General Public License as published by the Free 
 *  Software Foundation; either version 2 of the License, or (at your option) 
 *  any later version.
 * 
 *  This program is distributed in the hope that it will be useful, but 
 *  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 *  or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License 
 *  for more details.
 */

package megamek.client;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import megamek.common.*;
import megamek.common.actions.*;

public class PhysicalDisplay 
	extends AbstractPhaseDisplay
	implements BoardListener, GameListener, ActionListener,
	KeyListener, ComponentListener
{
    // parent game
    private Client          client;
    	
    // displays
    private BoardView		bv;
    private Label			labStatus;
    	
    // buttons
    private Container		panButtons;
    private Button			butPunch;
    private Button			butKick;
    private Button			butPush;
    private Button			butReady;
    private Button			butNext;
    private Button			butMenu;
    	
    // let's keep track of what we're shooting and at what, too
    private int				cen;		// current entity number
    private int				ten;		// target entity number
      
    // stuff we want to do
    private Vector          attacks;  
	
	/**
	 * Creates and lays out a new movement phase display 
	 * for the specified client.
	 */
	public PhysicalDisplay(Client client) {
		this.client = client;
		client.addGameListener(this);
		
		bv = client.bv;
		client.game.board.addBoardListener(this);
    
        attacks = new Vector();

        labStatus = new Label("Waiting to begin Physical Attack phase...", Label.CENTER);
	    	
		butPunch = new Button("Punch");
		butPunch.setActionCommand("punch");
		butPunch.addActionListener(this);
		butPunch.addKeyListener(this);
		butPunch.setEnabled(false);
		
		butKick = new Button("Kick");
		butKick.setActionCommand("kick");
		butKick.addActionListener(this);
		butKick.addKeyListener(this);
		butKick.setEnabled(false);
		
		butPush = new Button("Push");
		butPush.setActionCommand("push");
		butPush.addActionListener(this);
		butPush.addKeyListener(this);
		butPush.setEnabled(false);
		
		butReady = new Button("Done");
		butReady.setActionCommand("ready");
		butReady.addActionListener(this);
		butReady.addKeyListener(this);
		butReady.setEnabled(false);
		
		butNext = new Button("Next Unit");
		butNext.setActionCommand("next");
		butNext.addActionListener(this);
		butNext.addKeyListener(this);
		butNext.setEnabled(false);
		
		butMenu = new Button("?");
		butMenu.setActionCommand("menu");
		butMenu.addActionListener(this);
		butMenu.addKeyListener(this);
		butMenu.setEnabled(false);
		
		// layout button grid
		panButtons = new Panel();
		panButtons.setLayout(new GridLayout(2, 3));
		panButtons.add(butPunch);
		panButtons.add(butKick);
		panButtons.add(butNext);
		panButtons.add(butPush);
		panButtons.add(butMenu);
		panButtons.add(butReady);
	
		
		// layout screen
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		setLayout(gridbag);
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;	c.weighty = 1.0;
		c.insets = new Insets(1, 1, 1, 1);
		c.gridwidth = GridBagConstraints.REMAINDER;
		addBag(bv, gridbag, c);

		c.weightx = 1.0;	c.weighty = 0.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		addBag(labStatus, gridbag, c);

		c.gridwidth = 1;
		c.weightx = 1.0;	c.weighty = 0.0;
		addBag(client.cb.getComponent(), gridbag, c);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 0.0;	c.weighty = 0.0;
		addBag(panButtons, gridbag, c);
		
		addKeyListener(this);
		
		// mech display.
		client.frame.addComponentListener(this);
    
	}
	
	private void addBag(Component comp, GridBagLayout gridbag, GridBagConstraints c) {
		gridbag.setConstraints(comp, c);
		add(comp);
		comp.addKeyListener(this);
	}
	
	/**
	 * Selects an entity, by number, for movement.
	 */
	public void selectEntity(int en) {
        if (client.game.getEntity(en) != null) {
		    this.cen = en;
		    target(Entity.NONE);
		    client.game.board.highlight(ce().getPosition());
		    client.game.board.select(null);
		    client.game.board.cursor(null);
      
		    client.mechD.displayMech(ce());
		    client.mechD.showPanel("movement");
      
		    bv.centerOnHex(ce().getPosition());
        } else {
            System.err.println("FiringDisplay: tried to select non-existant entity: " + en);
            System.err.println("FiringDisplay: sending ready signal...");
	    	      client.sendReady(true);
        }
	}
	
	/**
	 * Does turn start stuff
	 */
	private void beginMyTurn() {
		ten = Entity.NONE;
		butNext.setEnabled(true);
		butReady.setEnabled(true);
		client.mechW.setVisible(true);
		moveMechDisplay();
		client.game.board.select(null);
		client.game.board.highlight(null);
		selectEntity(client.game.getFirstEntityNum(client.getLocalPlayer()));
	}
	
	/**
	 * Does end turn stuff.
	 */
	private void endMyTurn() {
		// end my turn, then.
		cen = Entity.NONE;
		ten = Entity.NONE;
		target(Entity.NONE);
		client.game.board.select(null);
		client.game.board.highlight(null);
		client.game.board.cursor(null);
		client.mechW.setVisible(false);
		bv.clearMovementData();
		butPush.setEnabled(false);
		butReady.setEnabled(false);
		butNext.setEnabled(false);
	}
	
	/**
	 * Called when the current entity is done firing.
	 */
	private void ready() {
        client.sendAttackData(cen, attacks);
        attacks.removeAllElements();
		client.sendEntityReady(cen);
		client.sendReady(true);
	}
	
    
    /**
     * Clears all current actions
     */
    private void clearattacks() {
        if (attacks.size() > 0) {
          attacks.removeAllElements();
        }
		client.mechD.wPan.displayMech(ce());
        //selectedWeapon = ce().getFirstWeapon();
        //client.mechD.wPan.selectWeapon(selectedWeapon);
		updateTarget();
    }
    
    /**
     * Punch the target!
     */
    private void punch() {
        final ToHitData leftArm = Compute.toHitPunch(client.game, cen, ten, PunchAttackAction.LEFT);
        final ToHitData rightArm = Compute.toHitPunch(client.game, cen, ten, PunchAttackAction.RIGHT);
        if (leftArm.getValue() != ToHitData.IMPOSSIBLE 
            && rightArm.getValue() != ToHitData.IMPOSSIBLE) {
            attacks.addElement(new PunchAttackAction(cen, ten, PunchAttackAction.BOTH));
        } else if (leftArm.getValue() < rightArm.getValue()) {
            attacks.addElement(new PunchAttackAction(cen, ten, PunchAttackAction.LEFT));
        } else {
            attacks.addElement(new PunchAttackAction(cen, ten, PunchAttackAction.RIGHT));
        }

        ready();
    }
	
    /**
     * Kick the target!
     */
    private void kick() {
        ToHitData leftLeg = Compute.toHitKick(client.game, cen, ten, KickAttackAction.LEFT);
        ToHitData rightLeg = Compute.toHitKick(client.game, cen, ten, KickAttackAction.RIGHT);
        
        if (leftLeg.getValue() < rightLeg.getValue()) {
            attacks.addElement(new KickAttackAction(cen, ten, PunchAttackAction.LEFT));
        } else {
            attacks.addElement(new KickAttackAction(cen, ten, PunchAttackAction.RIGHT));
        }
        ready();
    }
	
	/**
	 * Targets an entity
	 */
	private void target(int en) {
		this.ten = en;
		updateTarget();
	}
	
	/**
	 * Targets an entity
	 */
	private void updateTarget() {
		// dis/enable punch button
		if (cen != Entity.NONE && ten != Entity.NONE) {
            // punch?
            final ToHitData leftArm = Compute.toHitPunch(client.game, cen, ten, PunchAttackAction.LEFT);
            final ToHitData rightArm = Compute.toHitPunch(client.game, cen, ten, PunchAttackAction.RIGHT);
            boolean canPunch = leftArm.getValue() != ToHitData.IMPOSSIBLE 
                              || rightArm.getValue() != ToHitData.IMPOSSIBLE;
            butPunch.setEnabled(canPunch);
            
            // kick?
            ToHitData leftLeg = Compute.toHitKick(client.game, cen, ten, KickAttackAction.LEFT);
            ToHitData rightLeg = Compute.toHitKick(client.game, cen, ten, KickAttackAction.RIGHT);
            boolean canKick = leftLeg.getValue() != ToHitData.IMPOSSIBLE 
                              || rightLeg.getValue() != ToHitData.IMPOSSIBLE;
            butKick.setEnabled(canKick);
        } else {
            butPunch.setEnabled(false);
            butKick.setEnabled(false);
        }
	}
	
	/**
	 * Returns the current entity.
	 */
	private Entity ce() {
		return client.game.getEntity(cen);
	}
	
	/**
	 * Returns the target entity.
	 */
	private Entity te() {
		return client.game.getEntity(ten);
	}
	
	/**
	 * Moves the mech display window to the proper position.
	 */
	private void moveMechDisplay() {
		if (bv.isShowing()) {
			client.mechW.setLocation(bv.getLocationOnScreen().x + bv.getSize().width 
                               - client.mechD.getSize().width - 20, 
                               bv.getLocationOnScreen().y + 20);
		}
	}
	
	//
	// BoardListener
	//
	public void boardHexMoused(BoardEvent b) {
        if (client.isMyTurn()
            && (b.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) {
            if (b.getType() == b.BOARD_HEX_DRAGGED) {
                if (!b.getCoords().equals(client.game.board.lastCursor)) {
					client.game.board.cursor(b.getCoords());
				}
			} else if (b.getType() == b.BOARD_HEX_CLICKED) {
				client.game.board.select(b.getCoords());
			}
		}
	}
	public void boardHexSelected(BoardEvent b) {
        if (client.isMyTurn() && b.getCoords() != null && ce() != null) {
            if (client.game.getEntity(b.getCoords()) != null 
                && client.game.getEntity(b.getCoords()).isTargetable()
                && !b.getCoords().equals(ce().getPosition())) {
                target(client.game.getEntity(b.getCoords()).getId());
            } else {
                target(Entity.NONE);
            }
        }
	}
	
	//
	// GameListener
	//
	public void gameTurnChange(GameEvent ev) {
		if (client.game.phase == Game.PHASE_PHYSICAL) {
			endMyTurn();

            if (client.isMyTurn()) {
				beginMyTurn();
				labStatus.setText("It's your turn to declare physical attacks.");
			} else {
				labStatus.setText("It's " + ev.getPlayer().getName() + "'s turn to declare physical attacks.");
			}
        } else {
            System.err.println("PhysicalDisplay: got turnchange event when it's not the physical attacks phase");
        }
	}
	public void gamePhaseChange(GameEvent ev) {
		if (client.isMyTurn() && client.game.phase != Game.PHASE_PHYSICAL) {
			endMyTurn();
		}
		// if we're ending the firing phase, unregister stuff.
		if (client.game.phase !=  Game.PHASE_PHYSICAL) {
			bv.clearAllAttacks();
			
			client.removeGameListener(this);
			client.game.board.removeBoardListener(this);
			client.frame.removeComponentListener(this);
            client.bv.removeKeyListener(this);
            client.cb.getComponent().removeKeyListener(this);
		}
	}

	//
	// ActionListener
	//
	public void actionPerformed(ActionEvent ev) {
		if (ev.getActionCommand().equalsIgnoreCase("ready") && client.isMyTurn()) {
			ready();
		}
		if (ev.getActionCommand().equalsIgnoreCase("punch") && client.isMyTurn()) {
            punch();
		}
		if (ev.getActionCommand().equalsIgnoreCase("kick") && client.isMyTurn()) {
            kick();
		}
		if (ev.getActionCommand().equalsIgnoreCase("next") && client.isMyTurn()) {
			selectEntity(client.game.getNextEntityNum(client.getLocalPlayer(), cen));
		}
	}
	

	//
	// KeyListener
	//
	public void keyPressed(KeyEvent ev) {
		if (ev.getKeyCode() == ev.VK_ESCAPE) {
			clearattacks();
		}
		if (ev.getKeyCode() == ev.VK_ENTER && ev.isControlDown()) {
			if (client.isMyTurn()) {
				//
			}
		}
	}
	public void keyReleased(KeyEvent ev) {
        ;
	}
	public void keyTyped(KeyEvent ev) {
		;
	}
	
	//
	// ComponentListener
	//
	public void componentHidden(ComponentEvent ev) {
		client.mechW.setVisible(false);
	}
	public void componentMoved(ComponentEvent ev) {
		moveMechDisplay();
	}
	public void componentResized(ComponentEvent ev) {
		moveMechDisplay();
	}
	public void componentShown(ComponentEvent ev) {
		client.mechW.setVisible(false);
		moveMechDisplay();
	}
	
}
