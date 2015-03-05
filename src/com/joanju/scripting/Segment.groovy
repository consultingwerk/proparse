/* Segment.groovy
*/
package com.joanju.scripting;

class Segment {
	Segment(int startline, int startcol, int endline, int endcol) {
		this.startline = startline
		this.startcol = startcol
		this.endline = endline
		this.endcol = endcol
	}
	int startline, startcol, endline, endcol
}
