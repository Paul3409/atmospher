"""Rebuild original livery textures and synthetic two-tone siren. Pillow + ffmpeg required.
Builds consume committed assets directly; Python is NOT needed to compile the mod.
"""
from pathlib import Path
from PIL import Image, ImageDraw, ImageFont
import json, math, wave, struct, subprocess
ROOT = Path(__file__).resolve().parents[1]
ASSETS = ROOT / 'src/main/resources/assets/quellcode'
FONT = '/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf'
vehicles = [('mzf_elw','MZF / ELW','18'),('dlk_18','DLK 18','32'),('lf_10','LF 10','42'),('tsf_w','TSF-W','48'),('mzb','MZB','79')]
colors = ['#b51e28','#eeeedd','#22262b','#a9b2b9','#24414f','#b6bdc3','#e7ed58','#1456aa','#fff1c2','#c31825','#20252a','#b51e28','#b51e28','#606c76','#30353b','#eeeeea']
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
                d.line((0,y,255,y),fill=(181+int(8*(1-y/255)),30+int(5*(1-y/255)),40+int(5*(1-y/255))))
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
            for x in range(8,256,16): d.line((x,8,x,248),fill={7:'#3a8ce8',8:'#ffffed',9:'#ef3946'}[tile],width=4)
            d.rectangle((0,0,255,255),outline='#30373e',width=7)
        elif tile==10:
            for y in range(16,256,25): d.rectangle((0,y,255,y+5),fill='#7f8589')
        elif tile==11:
            d.rectangle((0,0,255,12),fill='#e7ed58'); d.rectangle((0,244,255,255),fill='#e7ed58')
            text_center(d,'FEUERWEHR',30,36)
            text_center(d,'Nebelthal' if vid=='mzb' else 'NEBELTHAL',91,35)
            text_center(d,f'FL NT 10/{number}-01',155,29)
            text_center(d,title+'  •  112',201,22)
        elif tile==12:
            text_center(d,'FEUERWEHR',20,36)
            text_center(d,'NEBELTHAL',86,34)
            text_center(d,f'FL NT 10/{number}-01',157,29)
            d.rectangle((0,235,255,255),fill='#e7ed58')
        elif tile==13:
            for y in range(0,256,20):
                for x in range(0,256,20): d.line((x,y,x+8,y+7),fill='#aab4bd',width=3)
        elif tile==15:
            d.rectangle((0,0,22,255),fill='#264c9a'); text_center(d,f'NT FW {number}',75,37,fill='#181c21')
            d.rectangle((1,1,254,254),outline='#222222',width=4)
        atlas.paste(im,((tile%4)*256,(tile//4)*256))
    atlas.save(ASSETS/'textures/entity'/f'{vid}.png')
    # High-contrast original inventory pictograms, matched to the common livery.
    icon=Image.new('RGBA',(64,64)); d=ImageDraw.Draw(icon)
    if vid=='mzb':
        d.polygon([(5,35),(57,35),(49,50),(17,50)],fill='#b51e28',outline='#252b30')
        d.rectangle((25,22,35,35),fill='#eeeedd'); d.rectangle((25,18,35,23),fill='#24414f')
        d.line((11,39,53,39),fill='#e7ed58',width=3)
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
    for vid,title,number in vehicles:
        for prefix in ['item','entity']: data[f'{prefix}.quellcode.{vid}']=f'{title} — FL NT 10/{number}-01'
    (ASSETS/'lang'/f'{lang}.json').write_text(json.dumps(data,indent=2,ensure_ascii=False),encoding='utf-8')
(ASSETS/'sounds.json').write_text(json.dumps({'martinshorn':{'subtitle':'subtitle.quellcode.martinshorn','sounds':[{'name':'quellcode:martinshorn','stream':False}]}},indent=2))
for lang in ['en_us','de_de']:
    path=ASSETS/'lang'/f'{lang}.json'; data=json.loads(path.read_text())
    data['subtitle.quellcode.martinshorn']='Martinshorn' if lang=='de_de' else 'Fire vehicle siren'
    path.write_text(json.dumps(data,indent=2,ensure_ascii=False),encoding='utf-8')
# Original synthesized German-style alternating fourth, not a sampled commercial horn.
wav=ROOT/'tools/martinshorn.wav'; rate=22050
with wave.open(str(wav),'wb') as out:
    out.setparams((1,2,rate,0,'NONE','not compressed'))
    samples=[]; phase=0
    for i in range(rate*2):
        t=i/rate; freq=435 if int(t/.5)%2==0 else 580
        phase+=2*math.pi*freq/rate
        envelope=min(1,(t%.5)/.02,(.5-t%.5)/.02)
        value=(math.sin(phase)+.3*math.sin(3*phase)+.12*math.sin(5*phase))*.45*envelope
        samples.append(struct.pack('<h',int(value*32767)))
    out.writeframes(b''.join(samples))
subprocess.run(['ffmpeg','-y','-loglevel','error','-i',str(wav),'-c:a','libvorbis','-q:a','4',str(ASSETS/'sounds/martinshorn.ogg')],check=True)
wav.unlink()
print('Generated five atlases, five inventory icons, item models, translations, and siren.')
