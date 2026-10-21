"""Rebuild the Nebelthal livery and the edited user-supplied siren. Pillow + ffmpeg required.
Builds consume committed assets directly; Python is NOT needed to compile the mod.
"""
from pathlib import Path
from PIL import Image, ImageDraw, ImageFont
import json, subprocess
ROOT = Path(__file__).resolve().parents[1]
ASSETS = ROOT / 'src/main/resources/assets/quellcode'
FONT = '/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf'
vehicles = [('mzf_elw','MZF / ELW','18'),('dlk_18','DLK 18','32'),('lf_10','LF 10','42'),('tsf_w','TSF-W','48'),('mzb','MZB','79'),('rw_2','RW 2','52')]
colors = ['#d71e27','#f0f0e9','#22262b','#adb7bd','#37505b','#b6bdc3','#f3e44b','#268fff','#ffffff','#d31928','#20252a','#d71e27','#d71e27','#68757e','#30353b','#eeeeea']
def text_center(draw, value, y, size, fill='white'):
    font = ImageFont.truetype(FONT,size)
    while draw.textbbox((0,0),value,font=font)[2]>240:
        size-=1; font=ImageFont.truetype(FONT,size)
    draw.text((128,y),value,font=font,fill=fill,anchor='mt')
for vid,title,number in vehicles:
    atlas=Image.new('RGB',(1024,1024))
    for tile,color in enumerate(colors):
        im=Image.new('RGB',(256,256),color); d=ImageDraw.Draw(im)
        if tile==0:
            for y in range(256):
                d.line((0,y,255,y),fill=(205+int(10*(1-y/255)),30+int(5*(1-y/255)),39+int(4*(1-y/255))))
        elif tile==2:
            for y in range(0,256,24): d.line((0,y,256,y+48),fill='#31363b',width=5)
        elif tile==3:
            for y in range(0,256,4): d.line((0,y,255,y),fill='#bbc3c9')
        elif tile==4:
            d.polygon([(12,0),(65,0),(230,255),(177,255)],fill='#375464')
            d.rectangle((0,0,255,255),outline='#151e26',width=9)
        elif tile==5:
            for y in range(0,256,12):
                d.line((0,y,255,y),fill='#777f86',width=2)
                d.line((0,y+3,255,y+3),fill='#dce0e2')
            d.rectangle((0,0,255,255),outline='#60676d',width=4)
        elif tile in (7,8,9):
            for x in range(8,256,16): d.line((x,8,x,248),fill={7:'#a2efff',8:'#ffffff',9:'#ef3946'}[tile],width=7)
            d.rectangle((0,0,255,255),outline='#30373e',width=7)
        elif tile==10:
            for y in range(16,256,25): d.rectangle((0,y,255,y+5),fill='#7f8589')
        elif tile==13:
            for y in range(0,256,20):
                for x in range(0,256,20): d.line((x,y,x+8,y+7),fill='#aab4bd',width=3)
        elif tile==15:
            d.rectangle((0,0,22,255),fill='#264c9a'); text_center(d,f'NT FW {number}',75,37,fill='#181c21')
            d.rectangle((1,1,254,254),outline='#222222',width=4)
        atlas.paste(im,((tile%4)*256,(tile//4)*256))
    atlas.save(ASSETS/'textures/entity'/f'{vid}.png')
    # One phrase per panel. Transparent margins keep letters from touching the edges.
    mark_dir=ASSETS/'textures/markings'; mark_dir.mkdir(parents=True,exist_ok=True)
    for suffix,value,dimensions in [('name','NEBELTHAL' if vid=='mzb' else 'FEUERWEHR NEBELTHAL',(512,80)),
                                    ('callsign',f'FL NT 10/{number}-01',(1024,80))]:
        panel=Image.new('RGBA',dimensions,(0,0,0,0)); pd=ImageDraw.Draw(panel)
        font_size=51 if suffix=='name' else 55
        font=ImageFont.truetype(FONT,font_size)
        while pd.textbbox((0,0),value,font=font)[2]>dimensions[0]-32:
            font_size-=1; font=ImageFont.truetype(FONT,font_size)
        pd.text((dimensions[0]//2,dimensions[1]//2),value,font=font,anchor='mm',fill='#fff45d',
                stroke_width=2,stroke_fill='#172328')
        panel.save(mark_dir/f'{vid}_{suffix}.png')
    # High-contrast original inventory pictograms, matched to the common livery.
    icon=Image.new('RGBA',(64,64)); d=ImageDraw.Draw(icon)
    if vid=='mzb':
        d.polygon([(5,35),(57,35),(49,50),(17,50)],fill='#b51e28',outline='#252b30')
        d.rectangle((25,22,35,35),fill='#eeeedd'); d.rectangle((25,18,35,23),fill='#24414f')
        d.line((11,39,53,39),fill='#e7ed58',width=3)
    elif vid=='mzf_elw':
        d.rounded_rectangle((6,19,58,46),7,fill='#d71e27')
        d.polygon([(8,28),(18,20),(29,20),(30,35),(8,35)],fill='#37505b')
        d.rectangle((36,25,53,35),fill='#37505b')
        d.line((7,39,57,39),fill='#eeeeea',width=3)
        for x in (17,48): d.ellipse((x-5,43,x+5,53),fill='#22262b')
    else:
        d.rounded_rectangle((5,21,59,47),3,fill='#b51e28')
        d.rectangle((8,24,22,33),fill='#24414f')
        d.rectangle((27,24,55,37),fill='#b6bdc3')
        for y in range(26,37,3): d.line((28,y,54,y),fill='#777f86')
        d.line((6,40,58,40),fill='#e7ed58',width=3)
        for x in (17,48): d.ellipse((x-6,42,x+6,54),fill='#22262b'); d.ellipse((x-3,45,x+3,51),fill='#a9b2b9')
        d.rectangle((10,17,19,21),fill='#2b8bff')
        if vid=='dlk_18':
            d.rectangle((20,11,58,15),fill='#b6bdc3')
            for x in range(22,58,5): d.line((x,10,x,17),fill='#777f86')
    icon.save(ASSETS/'textures/item'/f'{vid}.png')
    (ASSETS/'models/item'/f'{vid}.json').write_text(json.dumps({'parent':'minecraft:item/generated','textures':{'layer0':f'quellcode:item/{vid}'}},indent=2))
for lang in ['en_us','de_de']:
    data={'itemGroup.quellcode':'Feuerwehr Nebelthal','category.quellcode':'Quellcode — Nebelthal',
          'key.quellcode.lights':'Blaulicht' if lang=='de_de' else 'Blue lights',
          'key.quellcode.siren':'Martinshorn' if lang=='de_de' else 'Two-tone siren',
          'key.quellcode.ladder':'Drehleiter ausfahren / einfahren' if lang=='de_de' else 'Deploy / retract ladder',
          'message.quellcode.controls':'WASD: Fahren | B: Blaulicht | N: Horn | L: Leiter | Umschalt: Aussteigen' if lang=='de_de' else 'WASD: Drive | B: Lights | N: Siren | L: Ladder | Shift: Dismount'}
    data['item.quellcode.recovery_tool']='Fahrzeug-Bergungsschlüssel' if lang=='de_de' else 'Vehicle recovery tool'
    for vid,title,number in vehicles:
        for prefix in ['item','entity']: data[f'{prefix}.quellcode.{vid}']=f'{title} — FL NT 10/{number}-01'
    (ASSETS/'lang'/f'{lang}.json').write_text(json.dumps(data,indent=2,ensure_ascii=False),encoding='utf-8')
(ASSETS/'sounds.json').write_text(json.dumps({'martinshorn':{'subtitle':'subtitle.quellcode.martinshorn','sounds':[{'name':'quellcode:martinshorn','stream':False}]}},indent=2))
for lang in ['en_us','de_de']:
    path=ASSETS/'lang'/f'{lang}.json'; data=json.loads(path.read_text())
    data['subtitle.quellcode.martinshorn']='Martinshorn' if lang=='de_de' else 'Fire vehicle siren'
    path.write_text(json.dumps(data,indent=2,ensure_ascii=False),encoding='utf-8')
# The attached MP3 is converted to the OGG format Minecraft plays. The gentle
# fade removes the hard loop boundary without replacing the supplied recording.
source=ROOT/'tools/source_martinshorn.mp3'
if source.is_file():
    subprocess.run(['ffmpeg','-y','-loglevel','error','-i',str(source),'-af',
                    'highpass=f=170,lowpass=f=3800,afade=t=in:st=0:d=0.18,afade=t=out:st=6.95:d=0.3',
                    '-c:a','libvorbis','-q:a','5',str(ASSETS/'sounds/martinshorn.ogg')],check=True)
else:
    print('MP3 source missing; existing OGG remains unchanged.')
# Distinct wrench icon for recovering a vehicle without creative mode.
tool=Image.new('RGBA',(64,64)); td=ImageDraw.Draw(tool)
td.line((13,52,46,19),fill='#b7c2c9',width=10)
td.polygon([(40,23),(41,9),(52,7),(47,17),(53,23),(58,16),(58,29),(48,32)],fill='#d4dce0',outline='#32383d')
td.ellipse((10,48,20,58),fill='#303b46',outline='#d4dce0',width=2)
tool.save(ASSETS/'textures/item/recovery_tool.png')
(ASSETS/'models/item/recovery_tool.json').write_text(json.dumps({'parent':'minecraft:item/generated','textures':{'layer0':'quellcode:item/recovery_tool'}},indent=2))
brand=Image.new('RGBA',(512,80),(0,0,0,0)); bd=ImageDraw.Draw(brand)
bd.text((256,40),'FEUERWEHR',font=ImageFont.truetype(FONT,58),anchor='mm',fill='#fff45d',stroke_width=2,stroke_fill='#172328')
brand.save(ASSETS/'textures/markings/brand.png')
print('Generated six vehicle atlases, readable signs, seven inventory icons, and horn.')
