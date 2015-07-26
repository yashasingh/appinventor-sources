// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2011-2015 MIT, All rights reserved
// Released under the Apache License, Version 2.0
// http://www.apache.org/licenses/LICENSE-2.0

package com.google.appinventor.components.runtime;

import com.google.appinventor.components.annotations.DesignerComponent;
import com.google.appinventor.components.annotations.PropertyCategory;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.annotations.SimpleObject;
import com.google.appinventor.components.annotations.SimpleProperty;
import com.google.appinventor.components.annotations.UsesLibraries;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.common.PropertyTypeConstants;
import com.google.appinventor.components.common.YaVersion;
import com.google.appinventor.components.runtime.util.ErrorMessages;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.Servo.Direction;

/**
 * A component for a servo of an FTC robot.
 *
 * @author lizlooney@google.com (Liz Looney)
 */
@DesignerComponent(version = YaVersion.FTC_SERVO_COMPONENT_VERSION,
    description = "A component for a servo of an FTC robot.",
    category = ComponentCategory.FIRSTTECHCHALLENGE,
    nonVisible = true,
    iconName = "images/ftc.png")
@SimpleObject
@UsesLibraries(libraries = "FtcRobotCore.jar")
public final class FtcServo extends FtcHardwareDevice {

  private volatile Servo servo;

  /**
   * Creates a new FtcServo component.
   */
  public FtcServo(ComponentContainer container) {
    super(container.$form());
  }

  // Properties

  /**
   * Direction_FORWARD property getter.
   */
  @SimpleProperty(description = "The constant for Direction_FORWARD.",
      category = PropertyCategory.BEHAVIOR)
  public String Direction_FORWARD() {
    return Direction.FORWARD.toString();
  }

  /**
   * Direction_REVERSE property getter.
   */
  @SimpleProperty(description = "The constant for Direction_REVERSE.",
      category = PropertyCategory.BEHAVIOR)
  public String Direction_REVERSE() {
    return Direction.REVERSE.toString();
  }

  /**
   * Direction property getter.
   */
  @SimpleProperty(description = "Whether this servo should spin forward or reverse.",
      category = PropertyCategory.BEHAVIOR)
  public String Direction() {
    if (servo != null) {
      try {
        Direction direction = servo.getDirection();
        if (direction != null) {
          return direction.toString();
        }
      } catch (Throwable e) {
        e.printStackTrace();
        form.dispatchErrorOccurredEvent(this, "Direction",
            ErrorMessages.ERROR_FTC_UNEXPECTED_ERROR, e.toString());
      }
    }
    return "";
  }

  /**
   * Direction property setter.
   */
  @SimpleProperty
  public void Direction(String directionString) {
    if (servo != null) {
      try {
        try {
          int n = Integer.decode(directionString);
          if (n == 1) {
            servo.setDirection(Direction.FORWARD);
            return;
          }
          if (n == -1) {
            servo.setDirection(Direction.REVERSE);
            return;
          }
        } catch (NumberFormatException e) {
          // Code below will try to interpret directionString as a Direction enum string.
        }
        
        for (Direction direction : Direction.values()) {
          if (direction.toString().equalsIgnoreCase(directionString)) {
            servo.setDirection(direction);
            return;
          }
        }

        form.dispatchErrorOccurredEvent(this, "Direction",
            ErrorMessages.ERROR_FTC_INVALID_DIRECTION, directionString);
      } catch (Throwable e) {
        e.printStackTrace();
        form.dispatchErrorOccurredEvent(this, "Direction",
            ErrorMessages.ERROR_FTC_UNEXPECTED_ERROR, e.toString());
      }
    }
  }

  /**
   * Position property getter.
   */
  @SimpleProperty(description = "The current servo position, between 0.0 and 1.0.",
      category = PropertyCategory.BEHAVIOR)
  public double Position() {
    if (servo != null) {
      try {
        return servo.getPosition();
      } catch (Throwable e) {
        e.printStackTrace();
        form.dispatchErrorOccurredEvent(this, "Position",
            ErrorMessages.ERROR_FTC_UNEXPECTED_ERROR, e.toString());
      }
    }
    return 0.0;
  }

  /**
   * Position property setter.
   */
  @SimpleProperty
  public void Position(double position) {
    if (servo != null) {
      try {
        servo.setPosition(position);
      } catch (Throwable e) {
        e.printStackTrace();
        form.dispatchErrorOccurredEvent(this, "Position",
            ErrorMessages.ERROR_FTC_UNEXPECTED_ERROR, e.toString());
      }
    }
  }

  // Functions

  @SimpleFunction(description = "Sets the scale range of this servo.")
  public void ScaleRange(double min, double max) {
    if (servo != null) {
      try {
        servo.scaleRange(min, max);
      } catch (IllegalArgumentException e) {
        form.dispatchErrorOccurredEvent(this, "ScaleRange",
            ErrorMessages.ERROR_FTC_INVALID_SCALE_RANGE, min, min);
      } catch (Throwable e) {
        e.printStackTrace();
        form.dispatchErrorOccurredEvent(this, "ScaleRange",
            ErrorMessages.ERROR_FTC_UNEXPECTED_ERROR, e.toString());
      }
    }
  }

  // FtcRobotController.HardwareDevice implementation

  @Override
  public void initHardwareDevice(HardwareMap hardwareMap) {
    if (hardwareMap != null) {
      servo = hardwareMap.servo.get(getDeviceName());
      if (servo == null) {
        deviceNotFound("Servo", hardwareMap.servo);
      }
    }
  }

  @Override
  public void clearHardwareDevice() {
    servo = null;
  }
}
