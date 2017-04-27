/*-
 * ================================================================================
 * eCOMP Portal SDK
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ================================================================================
 */
package org.openecomp.portalsdk.core.menu;

import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.openecomp.portalsdk.core.util.SystemProperties;


/*
  MenuProperties contains a list of constants used during the creation,
  privilege screening, and rendering of the application menu.
*/
public class MenuProperties {
  private MenuProperties() {
    // cannot instantiate
  }

  @SuppressWarnings("rawtypes")
  private static HashMap menuProperties = new HashMap();

  // keys used to reference values in the menu.properties file
  public static final String WIDTH         	        = "width";
  public static final String LEFT_POSITION 	        = "left_position";
  public static final String TOP_POSITION  	        = "top_position";
  public static final String FONT_COLOR    	        = "font_color";
  public static final String MOUSEOVER_FONT_COLOR       = "mouseover_font_color";
  public static final String BACKGROUND_COLOR           = "background_color";
  public static final String MOUSEOVER_BACKGROUND_COLOR = "mouseover_background_color";
  public static final String BORDER_COLOR               = "border_color";
  public static final String SEPARATOR_COLOR            = "separator_color";
  public static final String IMAGE_SRC                  = "image_src";
  public static final String IMAGE_SRC_LEFT             = "image_src_left";
  public static final String IMAGE_SRC_OVER             = "image_src_over";
  public static final String IMAGE_SRC_LEFT_OVER        = "image_src_left_over";
  public static final String EVALUATE_UPON_TREE_SHOW    = "evaluate_upon_tree_show";
  public static final String EVALUATE_UPON_TREE_HIDE    = "evaluate_upon_tree_hide";
  public static final String TOP_IS_PERMANENT           = "top_is_permanent";
  public static final String TOP_IS_HORIZONTAL          = "top_is_horizontal";
  public static final String TREE_IS_HORIZONTAL         = "tree_is_horizontal";
  public static final String POSITION_UNDER             = "position_under";
  public static final String TOP_MORE_IMAGES_VISIBLE    = "top_more_images_visible";
  public static final String TREE_MORE_IMAGES_VISIBLE   = "tree_more_images_visible";
  public static final String RIGHT_TO_LEFT              = "right_to_left";
  public static final String DISPLAY_ON_CLICK           = "display_on_click";
  public static final String TOP_IS_VARIABLE_WIDTH      = "top_is_variable_width";
  public static final String TREE_IS_VARIABLE_WIDTH     = "tree_is_variable_width";
  public static final String TOP_KEEP_IN_WINDOW_X       = "top_keep_in_window_x";
  public static final String TOP_KEEP_IN_WINDOW_Y       = "top_keep_in_window_y";
  public static final String MENU_ID_ADMIN  	        = "menu_id_admin";
  public static final String MENU_ID_LOGOUT 	        = "menu_id_logout";
  public static final String MENU_FRAME 		= "menu_frame";
  public static final String MAIN_FRAME 		= "main_frame";
  public static final String NESTED_MAIN_FRAME		= "nested_main_frame";
  public static final String ROLE_FUNCTIONS_TAG         = "role_functions_tag";

  public static final String MAX_DISPLAYABLE_ADMIN_MENU_SORT_ORDER = "max_displayable_admin_menu_sort_order";
  public static final String MENU_PROPERTIES_FILENAME_KEY          = "menu_properties_filename";
  public static final String DEFAULT_SERVLET_NAME                  = "dispatcher";
  public static final String DEFAULT_TARGET                        = "_self";

  public static final String TOP_MENU_CLASS        = "top_menu_class";
  public static final String TOP_MENU_LINK_CLASS   = "top_menu_link_class";

  public static final String ON_MOUSE_OUT_TRAILER  = "on_mouse_out_trailer";
  public static final String ON_MOUSE_OVER_TRAILER = "on_mouse_over_trailer";
  public static final String ON_CLICK_TRAILER	   = "on_click_trailer";

  public static final String MENU_ID_PREFIX	   = "menu_id_prefix";


  @SuppressWarnings("unchecked")
  public static void loadFromFile(ServletContext servletContext, String filename, String menuSetName) throws IOException {
    Properties    p = new Properties();

    if (filename == null) {
      filename = SystemProperties.getProperty(SystemProperties.APPLICATION_MENU_PROPERTIES_NAME);
    }

    p.load(servletContext.getResourceAsStream(SystemProperties.getProperty(SystemProperties.MENU_PROPERTIES_FILE_LOCATION) + filename));
    menuProperties.put(menuSetName, p);
   } // loadMenuProperties

  public static String getProperty(String key) {
    return getProperty(key, SystemProperties.getProperty(SystemProperties.APPLICATION_MENU_SET_NAME));
  }

  public static String getProperty(String key, String menuSetName) {
    Properties p = (Properties)menuProperties.get(menuSetName);
    return p.getProperty(key);
  }

}
