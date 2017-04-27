// Create an overlay on the map from a projected image...
// Author. John D. Coryat 01/2008
// USNaviguide LLC - http://www.usnaviguide.com
// Thanks go to Mile Williams EInsert: http://econym.googlepages.com/einsert.js, Google's GOverlay Example and Bratliff's suggestion...
// Opacity code from TPhoto: http://gmaps.tommangan.us/addtphoto.html
// This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
//
// Parameters: 
//    imageUrl: URL of the image
//    bounds: Bounds object of image destination
//    addZoom: Added Zoom factor as a parameter to the imageUrl (include complete parameter, including separater like '?zoom='
//
	
function ProjectedOverlay(imageUrl, bounds, addZoom)
{
 this.url_ = imageUrl ;
 this.bounds_ = bounds ;
 this.addZ_ = addZoom ;				// Add the zoom to the image as a parameter

 // Is this IE, if so we need to use AlphaImageLoader

 this.ie = false ;
 var agent = navigator.userAgent.toLowerCase();
 if ((agent.indexOf("msie") > -1) && (agent.indexOf("opera") < 1))
 {
  this.ie = true ;
 }
}

ProjectedOverlay.prototype = new GOverlay();

ProjectedOverlay.prototype.initialize = function(map)
{
 var div = document.createElement("div") ;
 div.style.position = "absolute" ;
 div.setAttribute('id',this.id) ;
 map.getPane(G_MAP_MAP_PANE).appendChild(div) ;
 this.map_ = map ;
 this.div_ = div ;
 if( this.percentOpacity )
 {
  this.setOpacity(this.percentOpacity) ;
 }
}

// Remove the main DIV from the map pane

ProjectedOverlay.prototype.remove = function()
{
 this.div_.parentNode.removeChild(this.div_);
 delete(this.map) ;
 delete(this.div) ;
}

// Copy our data to a new ProjectedOverlay...

ProjectedOverlay.prototype.copy = function()
{
 return new ProjectedOverlay(this.url_, this.bounds_, this.addZ_);
}

// Redraw based on the current projection and zoom level...

ProjectedOverlay.prototype.redraw = function(force)
{
 // We only need to redraw if the coordinate system has changed
 if (!force)
 {
  return ;
 }

 var c1 = this.map_.fromLatLngToDivPixel(this.bounds_.getSouthWest());
 var c2 = this.map_.fromLatLngToDivPixel(this.bounds_.getNorthEast());

 // Now position our DIV based on the DIV coordinates of our bounds

 this.div_.style.width = Math.abs(c2.x - c1.x) + "px";
 this.div_.style.height = Math.abs(c2.y - c1.y) + "px";
 this.div_.style.left = Math.min(c2.x, c1.x) + "px";
 this.div_.style.top = Math.min(c2.y, c1.y) + "px";
 
 var url = this.url_ ;
 var extn = url.substring(url.length - 4, 4) ;

 if ( this.addZ_ )
 {
  url += this.addZ_ + this.map_.getZoom() ;
 }

 if (this.ie && extn.toLowerCase() == '.png')
 {
  var loader = "filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src='" + url + "', sizingMethod='scale');" ;
  this.div_.innerHTML = '<div style="height:' + this.div_.style.height + 'px; width:' + this.div_.style.width + 'px; ' + loader + '" ></div>' ;
 } else
 {
  this.div_.innerHTML = '<img src="' + url + '"  width=' + this.div_.style.width + ' height=' + this.div_.style.height + ' >' ;
 }
}

ProjectedOverlay.prototype.setOpacity=function(opacity)
{
 if (opacity < 0)
 {
  opacity = 0 ;
 }
 if(opacity > 100)
 {
  opacity = 100 ;
 }
 var c = opacity/100 ;
 var d = document.getElementById( this.id ) ;

 if (typeof(d.style.filter) =='string')
 {
  d.style.filter = 'alpha(opacity:' + opacity + ')' ;
 }
 if (typeof(d.style.KHTMLOpacity) == 'string' )
 {
  d.style.KHTMLOpacity = c ;
 }
 if (typeof(d.style.MozOpacity) == 'string')
 {
  d.style.MozOpacity = c ;
 }
 if (typeof(d.style.opacity) == 'string')
 {
  d.style.opacity = c ;
 }
}
