package scripts.equal_more_e5c78eb95

import static java.lang.Math.abs

if (!src.isType('/obj/structure/table'))
    return sprite

int junction = 0

oRange(src, 1).eachForTypeOnly('/obj/structure/table') { W ->
    if (abs(src.x - W.x) - abs(src.y - W.y)) {
        junction |= getDir(src, W)
    }
}

def basesprite
def line_v
def line_h
def center_4
def cropped
def source_sprite

basesprite = getCachedDmi(src.icon).getSprite('box')
line_v = getCachedDmi(src.icon).getSprite('line_v')
line_h = getCachedDmi(src.icon).getSprite('line_h')
center_4 = getCachedDmi(src.icon).getSprite('center_4')
source_sprite = getCachedDmi(src.icon).getSprite(src.icon_state)

int sourceIconWidth = source_sprite.getWidth()
int sourceIconHeight = source_sprite.getHeight()
	
int sourceIconWidthHalf = sourceIconWidth / 2
int sourceIconHeightHalf = sourceIconHeight / 2

if (basesprite) {
	if (junction == 0)
        return sprite
    else if (junction == 1){
		sprite.graphics.with {
			cropped = line_v.getSubimage(0, 0, sourceIconWidth, sourceIconHeightHalf)
			drawImage(cropped, 0, 0, null)
			dispose()
			}
        }
    else if (junction == 2){
		sprite.graphics.with {
			cropped = line_v.getSubimage(0, sourceIconHeightHalf, sourceIconWidth, sourceIconHeightHalf)
			drawImage(cropped, 0, sourceIconHeightHalf, null)
			dispose()
			}
        }
    else if (junction == 3){
		sprite.graphics.with {
			drawImage(line_v, 0, 0, null)
			dispose()
			}
        }
    else if (junction == 4){
		sprite.graphics.with {
			cropped = line_h.getSubimage(sourceIconWidthHalf, 0, sourceIconWidthHalf, sourceIconHeight)
			drawImage(cropped, sourceIconWidthHalf, 0, null)
			dispose()
			}
        }
    else if (junction == 5){
		sprite.graphics.with {
			cropped = line_v.getSubimage(0, 0, sourceIconWidth, sourceIconHeightHalf)
			drawImage(cropped, 0, 0, null)
			cropped = line_h.getSubimage(sourceIconWidthHalf, 0, sourceIconWidthHalf, sourceIconHeight)
			drawImage(cropped, sourceIconWidthHalf, 0, null)
			cropped = center_4.getSubimage(sourceIconWidthHalf, 0, sourceIconWidthHalf, sourceIconHeightHalf)
			drawImage(cropped, sourceIconWidthHalf, 0, null)
			dispose()
			}
        }
    else if (junction == 6){
		sprite.graphics.with {
			cropped = line_v.getSubimage(0, sourceIconHeightHalf, sourceIconWidth, sourceIconHeightHalf)
			drawImage(cropped, 0, sourceIconHeightHalf, null)
			cropped = line_h.getSubimage(sourceIconWidthHalf, 0, sourceIconWidthHalf, sourceIconHeight)
			drawImage(cropped, sourceIconWidthHalf, 0, null)
			cropped = center_4.getSubimage(sourceIconWidthHalf, sourceIconHeightHalf, sourceIconWidthHalf, sourceIconHeightHalf)
			drawImage(cropped, sourceIconWidthHalf, sourceIconHeightHalf, null)
			dispose()
			}
        }
    else if (junction == 7){
		sprite.graphics.with {
			drawImage(line_v, 0, 0, null)
			cropped = center_4.getSubimage(sourceIconWidthHalf, 0, sourceIconWidthHalf, sourceIconHeight)
			drawImage(cropped, sourceIconWidthHalf, 0, null)
			dispose()
			}
        }
    else if (junction == 8){
		sprite.graphics.with {
			cropped = line_h.getSubimage(0, 0, sourceIconWidthHalf, sourceIconHeight)
			drawImage(cropped, 0, 0, null)
			dispose()
			}
        }
    else if (junction == 9){
		sprite.graphics.with {
			cropped = line_v.getSubimage(0, 0, sourceIconWidth, sourceIconHeightHalf)
			drawImage(cropped, 0, 0, null)
			cropped = line_h.getSubimage(0, 0, sourceIconWidthHalf, sourceIconHeight)
			drawImage(cropped, 0, 0, null)
			cropped = center_4.getSubimage(0, 0, sourceIconWidthHalf, sourceIconHeightHalf)
			drawImage(cropped, 0, 0, null)
			dispose()
			}
        }
    else if (junction == 10){
		sprite.graphics.with {
			cropped = line_v.getSubimage(0, sourceIconHeightHalf, sourceIconWidth, sourceIconHeightHalf)
			drawImage(cropped, 0, sourceIconHeightHalf, null)
			cropped = line_h.getSubimage(0, 0, sourceIconWidthHalf, sourceIconHeight)
			drawImage(cropped, 0, 0, null)
			cropped = center_4.getSubimage(0, sourceIconHeightHalf, sourceIconWidthHalf, sourceIconHeightHalf)
			drawImage(cropped, 0, sourceIconHeightHalf, null)
			dispose()
			}
        }
    else if (junction == 11){
		sprite.graphics.with {
			drawImage(line_v, 0, 0, null)
			cropped = center_4.getSubimage(0, 0, sourceIconWidthHalf, sourceIconHeight)
			drawImage(cropped, 0, 0, null)
			dispose()
			}
        }
    else if (junction == 12){
		sprite.graphics.with {
			drawImage(line_h, 0, 0, null)
			dispose()
			}
        }
    else if (junction == 13){
		sprite.graphics.with {
			drawImage(line_h, 0, 0, null)
			cropped = center_4.getSubimage(0, 0, sourceIconWidth, sourceIconHeightHalf)
			drawImage(cropped, 0, 0, null)
			dispose()
			}
        }
    else if (junction == 14){
		sprite.graphics.with {
			drawImage(line_h, 0, 0, null)
			cropped = center_4.getSubimage(0, sourceIconHeightHalf, sourceIconWidth, sourceIconHeightHalf)
			drawImage(cropped, 0, sourceIconHeightHalf, null)
			dispose()
			}
        }
    else if (junction == 15){
		sprite.graphics.with {
			drawImage(center_4, 0, 0, null)
			dispose()
			}
        }
}
return sprite