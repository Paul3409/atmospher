package de.nebelthal.quellcode.client;

import de.nebelthal.quellcode.Quellcode;
import de.nebelthal.quellcode.VehicleEntity;
import de.nebelthal.quellcode.VehicleKind;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

/** Compact world-unit models based on the silhouettes in the supplied reference pictures. */
public final class VehicleRendererV2 extends EntityRenderer<VehicleEntity> {
    private record Part(float x, float y, float z, float w, float h, float d, int tile) {}
    private static final EnumMap<VehicleKind, List<Part>> MODELS = new EnumMap<>(VehicleKind.class);
    private static final int PAINT=0, WHITE=1, RUBBER=2, METAL=3, GLASS=4, SHUTTER=5,
            YELLOW=6, BLUE=7, HEADLAMP=8, TAILLAMP=9, GRILLE=10, DECK=13, DARK=14;
    static { for (VehicleKind kind:VehicleKind.values()) MODELS.put(kind, create(kind)); }

    public VehicleRendererV2(EntityRendererFactory.Context context) { super(context); shadowRadius = 1.3f; }
    @Override public Identifier getTexture(VehicleEntity entity) { return Quellcode.id("textures/entity/"+entity.kind.id+".png"); }
    @Override public void render(VehicleEntity entity, float yaw, float tickDelta, MatrixStack matrices,
                                 VertexConsumerProvider consumers, int light) {
        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180-yaw));
        VertexConsumer body=consumers.getBuffer(RenderLayer.getEntityCutoutNoCull(getTexture(entity)));
        for (Part part:MODELS.get(entity.kind)) box(matrices,body,part,light);
        if (entity.kind != VehicleKind.MZB) {
            float front=entity.kind==VehicleKind.MZF_ELW||entity.kind==VehicleKind.TSF_W?-1.65f:-1.8f;
            float back=switch(entity.kind){case MZF_ELW->1.65f;case TSF_W->1.78f;default->2.25f;};
            for(float x:new float[]{-entity.kind.width/2,entity.kind.width/2}) {
                wheel(matrices,body,x,.48f,front,light);
                wheel(matrices,body,x,.48f,back,light);
            }
        }
        marks(entity,matrices,consumers);
        beacons(entity,matrices,consumers,light);
        if (entity.kind==VehicleKind.DLK_18) ladder(matrices,body,entity.ladderExtension(),light);
        matrices.pop();
        super.render(entity,yaw,tickDelta,matrices,consumers,light);
    }
    private static List<Part> create(VehicleKind kind) {
        List<Part> p=new ArrayList<>();
        if(kind==VehicleKind.MZB) { boat(p); return p; }
        switch(kind) {
            case MZF_ELW -> van(p);
            case TSF_W -> tsf(p);
            case LF_10 -> truck(p,kind,2.15f,.05f,3.45f,3);
            case RW_2 -> truck(p,kind,1.6f,-1.0f,3.45f,4);
            case DLK_18 -> dlk(p);
            default -> { }
        }
        return p;
    }
    private static void add(List<Part> p,float x,float y,float z,float w,float h,float d,int tile) {
        p.add(new Part(x,y,z,w,h,d,tile));
    }
    private static void van(List<Part> p) {
        float w=1.9f;
        add(p,-.95f,.5f,-2.55f,w,.33f,5.05f,DARK);
        add(p,-.85f,.85f,-2.85f,1.7f,.65f,1.15f,PAINT); // short hood
        add(p,-.95f,1.08f,-1.8f,w,1.37f,1.5f,PAINT);
        add(p,-.95f,.85f,-.35f,w,1.72f,2.85f,PAINT); // long, enclosed radio/work area
        add(p,-.95f,2.54f,-.4f,w,.11f,2.92f,WHITE);
        add(p,-.85f,2.43f,-1.75f,1.7f,.12f,1.42f,PAINT);
        add(p,-.84f,1.52f,-2.87f,1.68f,.11f,.03f,GRILLE);
        add(p,-.95f,.75f,-2.95f,w,.21f,.2f,DARK);
        for(float x:new float[]{-.88f,.65f}) {
            add(p,x,1.05f,-2.88f,.23f,.18f,.04f,HEADLAMP);
            add(p,x,1.28f,-2.88f,.18f,.06f,.04f,BLUE);
        }
        for(float x:new float[]{-.97f,.94f}) {
            add(p,x,1.55f,-1.52f,.035f,.74f,.95f,GLASS);
            add(p,x,1.58f,-.02f,.035f,.7f,1.55f,GLASS);
            add(p,x,1.14f,.05f,.04f,.06f,2.3f,WHITE);
            add(p,x,.88f,-.3f,.04f,.09f,2.8f,WHITE);
            add(p,x,1.05f,.1f,.04f,.32f,1.65f,PAINT);
        }
        add(p,-.82f,1.59f,2.515f,1.64f,.65f,.03f,GLASS);
        add(p,-.95f,.74f,2.5f,w,.23f,.15f,DARK);
        add(p,.67f,2.67f,.45f,.035f,.5f,.04f,DARK); // radio antenna
        add(p,-.55f,2.65f,1.5f,1.1f,.07f,.45f,WHITE); // roof vent
    }
    private static void tsf(List<Part> p) {
        float w=2.05f;
        add(p,-w/2,.5f,-2.65f,w,.33f,5.55f,DARK);
        add(p,-.94f,.82f,-2.88f,1.88f,.7f,1.13f,PAINT);
        add(p,-w/2,1.0f,-1.78f,w,1.52f,1.85f,PAINT); // double cab
        add(p,-w/2+.09f,2.51f,-1.7f,w-.18f,.12f,1.76f,PAINT);
        add(p,-w/2,.78f,.12f,w,2.08f,2.78f,PAINT); // box body distinct from van
        add(p,-w/2,2.86f,.12f,w,.09f,2.78f,DARK);
        add(p,-w/2,.84f,.08f,w,.13f,2.82f,WHITE);
        add(p,-.73f,1.54f,-2.9f,1.46f,.2f,.04f,GRILLE);
        add(p,-w/2,.75f,-3.0f,w,.2f,.18f,DARK);
        for(float x:new float[]{-1.04f,1.01f}) {
            add(p,x,1.63f,-1.53f,.04f,.71f,.72f,GLASS);
            add(p,x,1.63f,-.54f,.04f,.71f,.54f,GLASS);
            add(p,x,.98f,-1.45f,.05f,.09f,1.4f,YELLOW);
            add(p,x,1.03f,.28f,.05f,1.39f,1.14f,SHUTTER);
            add(p,x,1.03f,1.54f,.05f,1.39f,1.16f,SHUTTER);
            add(p,x,2.45f,.18f,.05f,.13f,2.56f,YELLOW);
        }
        add(p,-.78f,.9f,2.91f,1.56f,1.6f,.035f,SHUTTER);
        add(p,-.78f,1.2f,-2.91f,.2f,.22f,.04f,HEADLAMP);
        add(p,.58f,1.2f,-2.91f,.2f,.22f,.04f,HEADLAMP);
    }
    private static void truck(List<Part> p,VehicleKind kind,float cabLength,float boxStart,float rear,int shutters) {
        float half=kind.width/2;
        add(p,-half,.5f,-2.75f,kind.width,.36f,rear+2.75f,DARK);
        add(p,-half,.85f,-2.73f,kind.width,2.15f,cabLength,PAINT);
        add(p,-half,2.99f,-2.72f,kind.width,.11f,cabLength,DARK);
        add(p,-half+.07f,1.9f,-2.76f,kind.width-.14f,.74f,.03f,GLASS);
        add(p,-half+.12f,1.4f,-2.77f,kind.width-.24f,.27f,.035f,PAINT);
        add(p,-.7f,1.07f,-2.79f,1.4f,.28f,.035f,GRILLE);
        add(p,-half,.7f,-2.92f,kind.width,.21f,.2f,WHITE);
        for(float x:new float[]{-half+.12f,half-.4f}) {
            add(p,x,.94f,-2.8f,.28f,.17f,.04f,HEADLAMP);
            add(p,x,1.12f,-2.81f,.14f,.07f,.04f,BLUE);
        }
        for(float x:new float[]{-half-.025f,half-.01f}) {
            add(p,x,1.81f,-2.23f,.035f,.75f,.72f,GLASS);
            if(kind==VehicleKind.LF_10) add(p,x,1.81f,-1.34f,.035f,.75f,.64f,GLASS);
            add(p,x,1.02f,-2.6f,.045f,.11f,cabLength-.1f,YELLOW);
            add(p,x,.85f,-2.18f,.055f,.23f,.54f,WHITE); // wheel arch accent
        }
        add(p,-half,.78f,boxStart,kind.width,2.26f,rear-boxStart,PAINT);
        add(p,-half,3.04f,boxStart,kind.width,.12f,rear-boxStart,DARK);
        for(float x:new float[]{-half-.02f,half-.01f}) {
            float spacing=(rear-boxStart-.18f)/shutters;
            for(int n=0;n<shutters;n++) {
                float z=boxStart+.09f+n*spacing;
                add(p,x,1.03f,z,.035f,1.52f,spacing-.07f,SHUTTER);
                add(p,x-.006f,1.05f,z+spacing-.17f,.045f,.11f,.1f,DARK);
                add(p,x-.006f,2.61f,z,.045f,.11f,spacing-.07f,YELLOW);
                add(p,x-.006f,1.0f,z,.045f,.1f,spacing-.07f,YELLOW);
            }
            add(p,x,2.76f,boxStart+.1f,.045f,.17f,rear-boxStart-.2f,PAINT);
        }
        add(p,-half,.67f,rear,kind.width,.24f,.16f,WHITE);
        add(p,-half+.22f,1.06f,rear+.02f,kind.width-.44f,1.48f,.035f,SHUTTER);
        for(float x:new float[]{-half+.1f,half-.32f}) add(p,x,.95f,rear+.045f,.2f,.15f,.04f,TAILLAMP);
        if(kind==VehicleKind.LF_10) {
            for(float x:new float[]{-.75f,-.52f}) add(p,x,3.19f,.1f,.13f,.13f,3.0f,DARK);
            for(float x:new float[]{.16f,.78f}) add(p,x,3.21f,.15f,.06f,.07f,2.92f,METAL);
            for(int n=0;n<9;n++) add(p,.16f,3.22f,.17f+n*.34f,.68f,.05f,.055f,METAL);
        } else {
            for(float x:new float[]{-.77f,.7f}) add(p,x,3.18f,-.8f,.08f,.1f,3.8f,METAL);
            add(p,-.75f,3.26f,2.15f,1.5f,.12f,.45f,WHITE); // rescue gear carrier
            add(p,-.22f,.65f,-2.96f,.44f,.19f,.19f,PAINT); // winch
        }
    }
    private static void dlk(List<Part> p) {
        float w=2.4f;
        add(p,-1.2f,.5f,-2.75f,w,.36f,5.95f,DARK);
        add(p,-1.2f,.85f,-2.7f,w,2.05f,1.95f,PAINT);
        add(p,-1.2f,2.9f,-2.7f,w,.12f,1.95f,DARK);
        add(p,-1.1f,1.87f,-2.73f,2.2f,.72f,.035f,GLASS);
        add(p,-.7f,1.16f,-2.74f,1.4f,.28f,.035f,GRILLE);
        add(p,-1.2f,.7f,-2.87f,w,.22f,.17f,WHITE);
        for(float x:new float[]{-1.21f,1.18f}) {
            add(p,x,1.88f,-2.16f,.035f,.72f,.72f,GLASS);
            add(p,x,1.1f,-1.9f,.04f,.11f,1.18f,YELLOW);
            add(p,x,1.04f,-.55f,.04f,1.13f,1.1f,SHUTTER);
            add(p,x,1.04f,.7f,.04f,.65f,2.3f,SHUTTER);
            add(p,x,1.73f,.7f,.05f,.12f,2.3f,YELLOW);
        }
        add(p,-1.2f,.8f,-.7f,w,1.1f,3.9f,PAINT); // low turntable bed
        add(p,-1.2f,1.91f,-.7f,w,.12f,3.9f,DECK);
        add(p,-.85f,2.03f,1.46f,1.7f,.22f,1.15f,METAL);
        add(p,-.7f,2.25f,1.53f,1.4f,.38f,1.05f,PAINT); // turntable at rear
        add(p,-1.2f,.7f,3.2f,w,.21f,.16f,WHITE);
        add(p,-1.0f,1.1f,3.22f,2.0f,.55f,.025f,SHUTTER);
    }
    private static void boat(List<Part> p) {
        add(p,-.93f,.1f,-1.85f,1.86f,.34f,3.75f,PAINT);
        add(p,-.72f,.11f,-2.38f,1.44f,.29f,.55f,PAINT);
        add(p,-.42f,.15f,-2.65f,.84f,.22f,.27f,PAINT);
        add(p,-.79f,.46f,-1.65f,1.58f,.07f,3.38f,DECK);
        for(float x:new float[]{-.99f,.79f}) {
            add(p,x,.38f,-1.85f,.2f,.39f,3.84f,PAINT);
            add(p,x-.01f,.76f,-1.85f,.22f,.09f,3.84f,RUBBER);
            add(p,x-.015f,.54f,-1.75f,.23f,.11f,3.6f,YELLOW);
        }
        add(p,-.9f,.42f,1.9f,1.8f,.4f,.19f,PAINT);
        add(p,-.3f,.51f,-.42f,.6f,.68f,.42f,WHITE);
        add(p,-.33f,1.18f,-.44f,.66f,.29f,.06f,GLASS);
        add(p,-.65f,.68f,.68f,1.3f,.13f,.43f,RUBBER);
        for(float x:new float[]{-.82f,.76f}) add(p,x,.8f,.42f,.06f,1.08f,.07f,METAL);
        add(p,-.82f,1.9f,.42f,1.64f,.07f,.07f,METAL);
        add(p,-.25f,.4f,2.1f,.5f,.73f,.37f,DARK);
        add(p,-.11f,-.12f,2.28f,.22f,.65f,.15f,DARK);
        add(p,-.28f,-.13f,2.4f,.56f,.07f,.08f,METAL);
    }
    private void marks(VehicleEntity entity,MatrixStack m,VertexConsumerProvider consumers) {
        String id=entity.kind.id;
        VertexConsumer brand=consumers.getBuffer(RenderLayer.getEntityCutoutNoCull(Quellcode.id("textures/markings/brand.png")));
        VertexConsumer name=consumers.getBuffer(RenderLayer.getEntityCutoutNoCull(Quellcode.id("textures/markings/"+id+"_name.png")));
        VertexConsumer call=consumers.getBuffer(RenderLayer.getEntityCutoutNoCull(Quellcode.id("textures/markings/"+id+"_callsign.png")));
        float half=entity.kind.width/2;
        if(entity.kind==VehicleKind.MZB) {
            sideText(m,name,-.998f,.58f,-1.55f,2.35f,.25f,false);
            sideText(m,name,.998f,.58f,-1.55f,2.35f,.25f,true);
            sideText(m,call,-.998f,.39f,-1.45f,2.1f,.17f,false);
            sideText(m,call,.998f,.39f,-1.45f,2.1f,.17f,true);
            return;
        }
        float front=entity.kind==VehicleKind.MZF_ELW?-2.87f:entity.kind==VehicleKind.TSF_W?-2.91f:-2.79f;
        float frontLabelY=entity.kind==VehicleKind.MZF_ELW||entity.kind==VehicleKind.TSF_W?1.24f:1.43f;
        float frontLabelHeight=frontLabelY==1.24f?.2f:.27f;
        frontText(m,brand,-.77f,frontLabelY,front-.006f,1.54f,frontLabelHeight,false);
        if(entity.kind==VehicleKind.MZF_ELW) {
            for(boolean right:new boolean[]{false,true}) {
                float x=right?half+.02f:-half-.02f;
                sideText(m,name,x,1.3f,.07f,2.16f,.34f,right);
                sideText(m,call,x,1.05f,.15f,2.06f,.24f,right);
            }
        } else if(entity.kind==VehicleKind.TSF_W) {
            for(boolean right:new boolean[]{false,true}) {
                float x=right?half+.07f:-half-.07f;
                sideText(m,name,x,2.56f,.24f,2.48f,.29f,right);
                sideText(m,call,x,.82f,.3f,2.32f,.17f,right);
            }
        } else if(entity.kind==VehicleKind.DLK_18) {
            for(boolean right:new boolean[]{false,true}) {
                float x=right?half+.07f:-half-.07f;
                sideText(m,name,x,1.38f,.78f,2.13f,.3f,right);
                sideText(m,call,x,.84f,.65f,2.3f,.18f,right);
            }
        } else {
            for(boolean right:new boolean[]{false,true}) {
                float x=right?half+.07f:-half-.07f;
                sideText(m,name,x,2.66f,.3f,2.65f,.3f,right);
                sideText(m,call,x,.83f,.36f,2.4f,.18f,right);
            }
        }
        // Rear text is fixed to the actual rear panel, never beyond the vehicle.
        float rear=entity.kind==VehicleKind.MZF_ELW?2.54f:entity.kind==VehicleKind.TSF_W?2.94f:entity.kind==VehicleKind.DLK_18?3.24f:3.49f;
        frontText(m,call,-.92f,1.42f,rear+.012f,1.84f,.22f,true);
    }
    private static void sideText(MatrixStack m,VertexConsumer v,float x,float y,float z,float length,float height,boolean right) {
        float[][] corners=right ? new float[][]{{x,y,z+length},{x,y,z},{x,y+height,z},{x,y+height,z+length}}
                : new float[][]{{x,y,z},{x,y,z+length},{x,y+height,z+length},{x,y+height,z}};
        quad(m,v,0xF000F0,corners,right?1:-1,0,0);
    }
    private static void frontText(MatrixStack m,VertexConsumer v,float x,float y,float z,float length,float height,boolean rear) {
        float[][] corners=rear ? new float[][]{{x,y,z},{x+length,y,z},{x+length,y+height,z},{x,y+height,z}}
                : new float[][]{{x+length,y,z},{x,y,z},{x,y+height,z},{x+length,y+height,z}};
        quad(m,v,0xF000F0,corners,0,0,rear?1:-1);
    }
    private void beacons(VehicleEntity e,MatrixStack m,VertexConsumerProvider consumers,int light) {
        VertexConsumer lit=consumers.getBuffer(RenderLayer.getEntityTranslucentEmissive(getTexture(e)));
        VertexConsumer unlit=consumers.getBuffer(RenderLayer.getEntityCutoutNoCull(getTexture(e)));
        float roof=e.kind==VehicleKind.MZB?1.98f:e.kind==VehicleKind.MZF_ELW?2.66f:e.kind==VehicleKind.TSF_W?2.95f:3.13f;
        float front=e.kind==VehicleKind.MZB?.42f:e.kind==VehicleKind.MZF_ELW?-1.24f:-2.25f;
        for(int side=0;side<2;side++) {
            boolean on=e.lightsOn()&&((e.age/3+side)%4<2);
            float x=side==0?-.86f:.55f;
            box(m,on?lit:unlit,new Part(x,roof,front,.31f,.22f,.31f,BLUE),on?0xF000F0:light);
            if(on) box(m,lit,new Part(x+.06f,roof+.18f,front-.02f,.19f,.08f,.04f,HEADLAMP),0xF000F0);
            if(e.kind!=VehicleKind.MZB) {
                float rear=e.kind==VehicleKind.MZF_ELW?2.25f:e.kind==VehicleKind.TSF_W?2.6f:3.05f;
                box(m,on?lit:unlit,new Part(x,roof-.08f,rear,.3f,.14f,.13f,BLUE),on?0xF000F0:light);
                if(on) box(m,lit,new Part(x+.06f,roof-.04f,rear+.12f,.18f,.08f,.03f,HEADLAMP),0xF000F0);
            }
        }
    }
    private static void ladder(MatrixStack m,VertexConsumer v,float extend,int light) {
        for(int side:new int[]{-1,1}) for(float z:new float[]{-.4f,2.62f}) {
            float x=side<0?-1.22f-extend*.72f:1.0f;
            box(m,v,new Part(x,.73f,z,.22f+extend*.72f,.13f,.18f,METAL),light);
            float foot=side<0?x:x+extend*.72f;
            if(extend>.01f) box(m,v,new Part(foot,.12f,z,.2f,.61f,.18f,DARK),light);
        }
        m.push();
        m.translate(0,2.55,1.98); // the rear-mounted turntable in the reference
        m.multiply(RotationAxis.POSITIVE_X.rotationDegrees(extend*58));
        for(int section=0;section<4;section++) {
            float start=-4.55f-section*extend*3.25f;
            float width=.88f-section*.1f;
            for(float x:new float[]{-width/2,width/2-.06f}) {
                box(m,v,new Part(x,section*.1f,start,.06f,.08f,4.7f,METAL),light);
                box(m,v,new Part(x,section*.1f+.3f,start,.045f,.045f,4.7f,METAL),light);
            }
            for(int i=0;i<16;i++) box(m,v,new Part(-width/2,section*.1f,start+i*.29f,width,.05f,.055f,METAL),light);
        }
        m.translate(0,.28,-4.5f-extend*9.75f);
        m.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-extend*58));
        box(m,v,new Part(-.54f,-.1f,-.18f,1.08f,.12f,.75f,METAL),light);
        for(float x:new float[]{-.54f,.48f}) box(m,v,new Part(x,0,-.18f,.06f,.68f,.75f,DARK),light);
        box(m,v,new Part(-.54f,.02f,-.18f,1.08f,.65f,.055f,DARK),light);
        m.pop();
    }
    private static void wheel(MatrixStack m,VertexConsumer v,float x,float y,float z,int light) {
        float outer=x+Math.signum(x)*.12f,inner=x-Math.signum(x)*.19f;
        for(int i=0;i<16;i++) {
            double a=i*Math.PI/8,b=(i+1)*Math.PI/8;
            float ya=y+(float)Math.sin(a)*.46f,za=z+(float)Math.cos(a)*.46f;
            float yb=y+(float)Math.sin(b)*.46f,zb=z+(float)Math.cos(b)*.46f;
            tileQuad(m,v,RUBBER,light,new float[][]{{inner,ya,za},{outer,ya,za},{outer,yb,zb},{inner,yb,zb}},0,(float)Math.sin(a),(float)Math.cos(a));
            tileQuad(m,v,RUBBER,light,new float[][]{{outer,y,z},{outer,ya,za},{outer,yb,zb},{outer,y,z}},Math.signum(x),0,0);
        }
        box(m,v,new Part(outer-.035f,y-.22f,z-.22f,.07f,.44f,.44f,METAL),light);
        box(m,v,new Part(outer-.045f,y-.075f,z-.075f,.09f,.15f,.15f,DARK),light);
    }
    private static void box(MatrixStack m,VertexConsumer v,Part p,int light) {
        float x=p.x,y=p.y,z=p.z,X=x+p.w,Y=y+p.h,Z=z+p.d;
        tileQuad(m,v,p.tile,light,new float[][]{{x,y,z},{x,y,Z},{x,Y,Z},{x,Y,z}},-1,0,0);
        tileQuad(m,v,p.tile,light,new float[][]{{X,y,Z},{X,y,z},{X,Y,z},{X,Y,Z}},1,0,0);
        tileQuad(m,v,p.tile,light,new float[][]{{X,y,z},{x,y,z},{x,Y,z},{X,Y,z}},0,0,-1);
        tileQuad(m,v,p.tile,light,new float[][]{{x,y,Z},{X,y,Z},{X,Y,Z},{x,Y,Z}},0,0,1);
        tileQuad(m,v,p.tile,light,new float[][]{{x,Y,Z},{X,Y,Z},{X,Y,z},{x,Y,z}},0,1,0);
        tileQuad(m,v,p.tile,light,new float[][]{{x,y,z},{X,y,z},{X,y,Z},{x,y,Z}},0,-1,0);
    }
    private static void tileQuad(MatrixStack m,VertexConsumer v,int tile,int light,float[][] pts,float nx,float ny,float nz) {
        float u=(tile%4)*.25f,t=(tile/4)*.25f,in=.0005f;
        float[][] uv={{u+in,t+.25f-in},{u+.25f-in,t+.25f-in},{u+.25f-in,t+in},{u+in,t+in}};
        MatrixStack.Entry entry=m.peek();
        for(int i=0;i<4;i++) v.vertex(entry.getPositionMatrix(),pts[i][0],pts[i][1],pts[i][2])
                .color(255,255,255,255).texture(uv[i][0],uv[i][1]).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(entry,nx,ny,nz);
    }
    private static void quad(MatrixStack m,VertexConsumer v,int light,float[][] pts,float nx,float ny,float nz) {
        float[][] uv={{0,1},{1,1},{1,0},{0,0}};
        MatrixStack.Entry entry=m.peek();
        for(int i=0;i<4;i++) v.vertex(entry.getPositionMatrix(),pts[i][0],pts[i][1],pts[i][2])
                .color(255,255,255,255).texture(uv[i][0],uv[i][1]).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(entry,nx,ny,nz);
    }
}
