package de.nebelthal.quellcode.client;

import de.nebelthal.quellcode.*;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

/** World-unit bodywork. Every vehicle shares the exact same material atlas palette. */
public final class VehicleRenderer extends EntityRenderer<VehicleEntity> {
    // Atlas cells: paint, white, rubber, alloy, glazing, shutter, yellow, blue,
    // headlight, red lamp, grille, side lettering, rear lettering, roof, dark, plate.
    private record Part(float x, float y, float z, float w, float h, float d, int material) {}
    private static final EnumMap<VehicleKind, List<Part>> BODIES = new EnumMap<>(VehicleKind.class);
    static { for (VehicleKind kind : VehicleKind.values()) BODIES.put(kind, body(kind)); }
    public VehicleRenderer(EntityRendererFactory.Context context) { super(context); shadowRadius = 1.6f; }
    @Override public Identifier getTexture(VehicleEntity entity) { return Quellcode.id("textures/entity/" + entity.kind.id + ".png"); }

    @Override public void render(VehicleEntity entity, float yaw, float tickDelta, MatrixStack matrices,
                                 VertexConsumerProvider consumers, int light) {
        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180 - yaw));
        VertexConsumer vertex = consumers.getBuffer(RenderLayer.getEntityCutoutNoCull(getTexture(entity)));
        for (Part part : BODIES.get(entity.kind)) box(matrices, vertex, part, light);
        boolean boat = entity.kind == VehicleKind.MZB;
        if (!boat) {
            float width = entity.kind.width;
            float rear = entity.kind == VehicleKind.MZF_ELW ? 1.45f : entity.kind == VehicleKind.TSF_W ? 1.4f : 1.8f;
            for (float x : new float[] {-width / 2, width / 2}) {
                wheel(matrices, vertex, x, .48f, -1.65f, light);
                wheel(matrices, vertex, x, .48f, rear, light);
            }
        }
        float roof = boat ? 2.05f : entity.kind == VehicleKind.MZF_ELW ? 2.65f : 2.95f;
        for (int side = 0; side < 2; side++) {
            boolean flash = entity.lightsOn() && ((entity.age / 3 + side) % 4 < 2);
            box(matrices, vertex, new Part(side == 0 ? -.88f : .5f, roof, boat ? .3f : -1.6f, .38f, .16f, .28f, 7), flash ? 0xF000F0 : light);
        }
        if (entity.kind == VehicleKind.DLK_18) ladder(matrices, vertex, entity.ladderExtension(), light);
        matrices.pop();
        super.render(entity, yaw, tickDelta, matrices, consumers, light);
    }
    private static void add(List<Part> parts, float x, float y, float z, float w, float h, float d, int material) {
        parts.add(new Part(x,y,z,w,h,d,material));
    }
    private static List<Part> body(VehicleKind kind) {
        List<Part> p = new ArrayList<>();
        if (kind == VehicleKind.MZB) {
            add(p,-.95f,.12f,-1.7f,1.9f,.28f,3.7f,0);
            add(p,-.72f,.14f,-2.25f,1.44f,.25f,.55f,0);
            add(p,-.4f,.18f,-2.6f,.8f,.22f,.35f,0);
            add(p,-.8f,.42f,-1.55f,1.6f,.06f,3.3f,13);
            for (float x : new float[] {-.99f,.8f}) {
                add(p,x,.4f,-1.8f,.19f,.38f,3.9f,0);
                add(p,x-.025f,.77f,-1.8f,.24f,.10f,3.9f,2);
                add(p,x-.007f,.52f,-1.6f,.204f,.1f,3.5f,6);
                add(p,x-.012f,.60f,-1.45f,.214f,.16f,2.8f,11);
            }
            add(p,-.7f,.45f,-2.1f,1.4f,.3f,.18f,0);
            add(p,-.95f,.4f,1.9f,1.9f,.4f,.2f,0);
            add(p,-.3f,.48f,-.4f,.6f,.7f,.45f,1);
            add(p,-.32f,1.18f,-.43f,.64f,.3f,.06f,4);
            add(p,-.7f,.65f,.65f,1.4f,.12f,.45f,2);
            for (float x : new float[] {-.85f,.79f}) add(p,x,.8f,.4f,.06f,1.25f,.07f,3);
            add(p,-.85f,2.01f,.4f,1.7f,.07f,.07f,3);
            add(p,-.25f,.45f,2.08f,.5f,.75f,.45f,14);
            add(p,-.1f,-.2f,2.23f,.2f,.8f,.16f,14);
            add(p,-.27f,-.15f,2.35f,.54f,.07f,.06f,3);
            add(p,-.35f,.83f,-1.5f,.7f,.07f,.08f,3);
            return p;
        }
        float w = kind.width, half = w / 2;
        boolean van = kind == VehicleKind.MZF_ELW;
        boolean ladder = kind == VehicleKind.DLK_18;
        float rear = van ? 2.5f : kind == VehicleKind.TSF_W ? 2.65f : 3.25f;
        float height = van ? 2.55f : 2.85f;
        add(p,-half,.45f,-2.6f,w,.35f,rear+2.6f,14);
        add(p,-half,.8f,-2.55f,w,1.0f,2.2f,0);
        add(p,-half+.06f,1.8f,-2.25f,w-.12f,height-1.8f,1.9f,0);
        add(p,-half+.13f,1.87f,-2.265f,w-.26f,.57f,.025f,4);
        add(p,-.025f,1.85f,-2.29f,.05f,.62f,.04f,1);
        add(p,-half+.04f,height,-2.28f,w-.08f,.1f,1.95f,1);
        add(p,-half,.77f,-2.7f,w,.22f,.17f,1);
        add(p,-.65f,1.02f,-2.57f,1.3f,.38f,.035f,10);
        add(p,-.28f,.8f,-2.72f,.56f,.12f,.02f,15);
        add(p,-.7f,1.47f,-2.57f,1.4f,.20f,.02f,12);
        for (float x : new float[] {-half+.09f,half-.49f}) {
            add(p,x,1.02f,-2.59f,.4f,.24f,.03f,8);
            add(p,x,1.29f,-2.59f,.15f,.07f,.03f,7);
        }
        for (int s : new int[] {-1,1}) {
            float side = s < 0 ? -half-.015f : half-.015f;
            add(p,side,1.88f,-2.03f,.03f,.55f,1.47f,4);
            add(p,side,1.2f,-1.95f,.035f,.55f,1.35f,11);
            add(p,side,1.02f,-2.35f,.04f,.10f,1.98f,6);
            add(p,side,1.71f,-.8f,.045f,.04f,.2f,3);
            add(p,s < 0 ? -half-.23f : half,1.75f,-2.0f,.23f,.05f,.08f,14);
            add(p,s < 0 ? -half-.28f : half+.18f,1.75f,-2.02f,.1f,.35f,.15f,14);
            add(p,s < 0 ? -half-.07f : half-.05f,.53f,-1.3f,.12f,.12f,.75f,3);
        }
        if (van) {
            add(p,-half,.8f,-.35f,w,1.75f,2.85f,0);
            add(p,-half,2.55f,-.4f,w,.1f,2.95f,1);
            for (float x : new float[] {-half-.01f,half-.02f}) {
                add(p,x,1.85f,-.1f,.03f,.55f,1.7f,4);
                add(p,x,1.15f,.0f,.04f,.5f,2.0f,11);
                add(p,x,1.0f,-.35f,.05f,.1f,2.85f,6);
            }
            add(p,-.75f,1.8f,2.51f,1.5f,.55f,.02f,4);
            add(p,.55f,2.65f,.5f,.025f,.65f,.025f,14);
            add(p,-.5f,2.65f,1.5f,1,.08f,.5f,1);
        } else if (!ladder) {
            add(p,-half,.8f,-.25f,w,2.1f,rear+.25f,0);
            add(p,-half,2.9f,-.25f,w,.10f,rear+.25f,3);
            for (float x : new float[] {-half-.015f,half-.015f}) {
                int compartments = kind == VehicleKind.TSF_W ? 2 : 3;
                float spacing = (rear + .1f) / compartments;
                for (int n=0;n<compartments;n++) {
                    float z=-.12f+n*spacing;
                    add(p,x,1.15f,z,.03f,1.28f,spacing-.12f,5);
                    add(p,x-.005f,1.22f,z+.32f,.04f,.055f,.34f,14);
                }
                add(p,x,2.5f,-.1f,.04f,.29f,rear-.15f,11);
                add(p,x,.93f,-.25f,.05f,.1f,rear+.25f,6);
            }
            add(p,-.82f,1.15f,rear+.01f,1.64f,1.32f,.03f,5);
            // Roof-mounted suction hoses and portable ladder.
            for (float x : new float[] {-.8f,-.55f}) add(p,x,3.02f,.0f,.16f,.16f,2.7f,14);
            for (float x : new float[] {.15f,.7f}) add(p,x,3.06f,-.1f,.065f,.08f,3.0f,3);
            for (int i=0;i<10;i++) add(p,.15f,3.06f,i*.29f,.61f,.05f,.055f,3);
        } else {
            add(p,-half,.8f,-.25f,w,.85f,rear+.25f,0);
            for (float x : new float[] {-half-.01f,half-.02f}) {
                add(p,x,1.0f,0,.03f,.5f,3,5);
                add(p,x,1.53f,0,.04f,.1f,3,6);
            }
            add(p,-.7f,1.65f,.5f,1.4f,.3f,1.4f,3);
            add(p,-.45f,1.95f,.7f,.9f,.4f,1.0f,0);
        }
        add(p,-half,.65f,rear,w,.22f,.16f,1);
        add(p,-.75f,van?1.12f:2.55f,rear+.035f,1.5f,.28f,.025f,12);
        add(p,-.28f,.73f,rear+.17f,.56f,.12f,.02f,15);
        for (float x : new float[] {-half+.05f,half-.25f}) add(p,x,.95f,rear+.035f,.2f,.18f,.03f,9);
        return p;
    }
    private static void ladder(MatrixStack m, VertexConsumer v, float extension, int light) {
        // Four stabilizers extend before the ladder reaches working height.
        for (int side : new int[] {-1,1}) for (float z : new float[] {-.1f,2.6f}) {
            float x = side < 0 ? -1.15f-extension*.85f : .85f;
            box(m,v,new Part(x,.62f,z,.3f+extension*.85f,.16f,.18f,1),light);
            float footX = side < 0 ? x : x+extension*.85f;
            box(m,v,new Part(footX,.08f,z,.3f,.54f,.18f,3),light);
        }
        m.push(); m.translate(0,2.25,1.0);
        m.multiply(RotationAxis.POSITIVE_X.rotationDegrees(extension*58));
        for (int section=0; section<4; section++) {
            float start = -3.4f-section*extension*4.6f;
            float width = .9f-section*.10f;
            for (float x : new float[] {-width/2,width/2-.055f}) {
                box(m,v,new Part(x,section*.12f,start,.055f,.09f,5.0f,3),light);
                box(m,v,new Part(x,section*.12f+.35f,start,.045f,.045f,5.0f,3),light);
            }
            for (int i=0;i<17;i++) {
                box(m,v,new Part(-width/2,section*.12f,start+i*.3f,width,.055f,.055f,3),light);
                for (float x : new float[] {-width/2,width/2-.04f})
                    box(m,v,new Part(x,section*.12f,start+i*.3f,.04f,.38f,.04f,3),light);
            }
        }
        m.translate(0,.24,-3.4-extension*13.8);
        m.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-extension*58));
        box(m,v,new Part(-.65f,-.10f,-.75f,1.3f,.12f,.85f,3),light);
        box(m,v,new Part(-.65f,0,-.75f,1.3f,.65f,.06f,1),light);
        for(float x:new float[]{-.65f,.59f}) box(m,v,new Part(x,0,-.75f,.06f,.65f,.85f,1),light);
        m.pop();
    }
    private static void wheel(MatrixStack m, VertexConsumer v, float x, float y, float z, int light) {
        float outer = x + Math.signum(x)*.10f, inner = x-Math.signum(x)*.18f;
        for(int i=0;i<16;i++) {
            double a=i*Math.PI/8, b=(i+1)*Math.PI/8;
            float ya=y+(float)Math.sin(a)*.47f, za=z+(float)Math.cos(a)*.47f;
            float yb=y+(float)Math.sin(b)*.47f, zb=z+(float)Math.cos(b)*.47f;
            quad(m,v,2,light,new float[][]{{inner,ya,za},{outer,ya,za},{outer,yb,zb},{inner,yb,zb}},0,(float)Math.sin(a),(float)Math.cos(a));
            quad(m,v,2,light,new float[][]{{outer,y,z},{outer,ya,za},{outer,yb,zb},{outer,y,z}},Math.signum(x),0,0);
        }
        box(m,v,new Part(outer-.035f,y-.22f,z-.22f,.07f,.44f,.44f,3),light);
        box(m,v,new Part(outer-.045f,y-.08f,z-.08f,.09f,.16f,.16f,14),light);
    }
    private static void box(MatrixStack m, VertexConsumer v, Part p, int light) {
        float x=p.x,y=p.y,z=p.z,X=x+p.w,Y=y+p.h,Z=z+p.d; int t=p.material;
        quad(m,v,t,light,new float[][]{{x,y,z},{x,y,Z},{x,Y,Z},{x,Y,z}},-1,0,0);
        quad(m,v,t,light,new float[][]{{X,y,Z},{X,y,z},{X,Y,z},{X,Y,Z}},1,0,0);
        quad(m,v,t,light,new float[][]{{X,y,z},{x,y,z},{x,Y,z},{X,Y,z}},0,0,-1);
        quad(m,v,t,light,new float[][]{{x,y,Z},{X,y,Z},{X,Y,Z},{x,Y,Z}},0,0,1);
        quad(m,v,t,light,new float[][]{{x,Y,Z},{X,Y,Z},{X,Y,z},{x,Y,z}},0,1,0);
        quad(m,v,t,light,new float[][]{{x,y,z},{X,y,z},{X,y,Z},{x,y,Z}},0,-1,0);
    }
    private static void quad(MatrixStack m, VertexConsumer v, int tile, int light, float[][] points, float nx,float ny,float nz) {
        float u=(tile%4)*.25f, t=(tile/4)*.25f, inset=.0005f;
        float[][] uv={{u+inset,t+.25f-inset},{u+.25f-inset,t+.25f-inset},{u+.25f-inset,t+inset},{u+inset,t+inset}};
        MatrixStack.Entry entry=m.peek();
        for(int i=0;i<4;i++) v.vertex(entry.getPositionMatrix(),points[i][0],points[i][1],points[i][2])
            .color(255,255,255,255).texture(uv[i][0],uv[i][1]).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(entry,nx,ny,nz);
    }
}
