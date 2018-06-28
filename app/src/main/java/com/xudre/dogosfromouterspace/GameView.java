package com.xudre.dogosfromouterspace;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;

public class GameView extends SurfaceView implements Runnable {

    public MainActivity Main;

    public int Margin = 32;

    public int Background = 0xFF25BF46;
    public int Width;
    public int Height;
    public boolean Paused;
    public int Score = 0;

    protected Context m_Context;
    protected SurfaceHolder m_Holder;
    protected Thread m_Thread;
    protected Paint m_Paint;

    private boolean m_Running;

    final private static int MAX_DOGOS = 10;
    final private static int MAX_SHOOTS = 20;
    final private static float SHOOT_INTERVAL = 200; // Milisegundos
    final private static float ENEMY_INTERVAL = 2000; // Milisegundos

    private Bitmap m_BackgroundImage;
    private Bitmap m_PlayerImage;
    private Bitmap m_EnemyImage;
    private Bitmap m_ShootImage;
    private Bitmap m_ControlSteerImage;
    private Bitmap m_ControlShootImage;

    private RectF m_BackgroundFrame;
    private float m_BackgroundHeight;
    private Shoot[] m_Shoots;
    private Enemy[] m_Enemies;
    private Player m_Player;

    private Button m_ControlLeft;
    private Button m_ControlRight;
    private Button m_ControlShoot;

    private int m_FrameCount = 0;
    private float m_Time;
    private float m_LastFrameTime;
    private float m_LastShootTime;
    private float m_LastEnemyTime;

    //region Contrutores da classe
    public GameView(Context context) {
        super(context);

        Start(context);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);

        Start(context);
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Start(context);
    }
    //endregion

    @Override
    public void run() {
        Update();
    }

    public void Stop() {

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        Width = w;
        Height = h;

        m_BackgroundFrame = new RectF(0, -m_BackgroundImage.getHeight(), w, h);

        int controlPosY = Height - m_ControlSteerImage.getHeight() - Margin;

        m_ControlLeft.Position.x = Margin;
        m_ControlLeft.Position.y = controlPosY;

        m_ControlShoot.Position.x = (Width - m_ControlShootImage.getWidth()) / 2;
        m_ControlShoot.Position.y = controlPosY + Margin;

        m_ControlRight.Position.x = Width - m_ControlSteerImage.getWidth() - Margin;
        m_ControlRight.Position.y = controlPosY;

        m_Player.Position.x = (Width - m_PlayerImage.getWidth()) / 2;
        m_Player.Position.y = controlPosY - m_PlayerImage.getHeight() - Margin;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        m_ControlLeft.ProcessEvents(event);
        m_ControlRight.ProcessEvents(event);
        m_ControlShoot.ProcessEvents(event);

        return true;
    }

    protected void Start(Context context) {
        m_Context = context;
        m_Holder = getHolder();

        m_Thread = new Thread(this);

        m_Thread.setName("GameView");
        m_Thread.setPriority(Thread.MAX_PRIORITY);

        m_Paint = new Paint();

        m_Paint.setTextSize(42);
        m_Paint.setColor(Color.WHITE);

        m_Thread.start();

        m_Running = true;

        Resources res = getResources();

        // Faz o cache das imagens utilizadas:
        m_BackgroundImage = BitmapFactory.decodeResource(res, R.drawable.game_background);
        m_BackgroundHeight = m_BackgroundImage.getHeight();

        m_PlayerImage = BitmapFactory.decodeResource(res, R.drawable.game_player);
        m_EnemyImage = BitmapFactory.decodeResource(res, R.drawable.game_enemy);
        m_ShootImage = BitmapFactory.decodeResource(res, R.drawable.game_shoot);

        m_Shoots = new Shoot[MAX_SHOOTS];
        m_Enemies = new Enemy[MAX_DOGOS];
        m_Player = new Player(m_PlayerImage);

        m_ControlSteerImage = BitmapFactory.decodeResource(res, R.drawable.game_control_steer);
        m_ControlShootImage = BitmapFactory.decodeResource(res, R.drawable.game_control_shoot);

        m_ControlLeft = new Button(m_ControlSteerImage);
        m_ControlRight = new Button(m_ControlSteerImage);
        m_ControlShoot = new Button(m_ControlShootImage);

        m_ControlRight.Rotation = 180;
    }

    protected void Update() {
        if (Paused) return;

        m_LastFrameTime = System.nanoTime() / 1000000;
        m_LastShootTime = m_LastFrameTime;

        while(m_Running) {
            m_Time = System.nanoTime() / 1000000;
            float deltaTime = (m_Time - m_LastFrameTime);

            m_LastFrameTime = m_Time;

            if (!m_Holder.getSurface().isValid()) continue;

            m_FrameCount++;

            //region Atualiza os elementos na tela

            // Rolagem da imagem de fundo:
            m_BackgroundFrame.top += 1;

            if (m_BackgroundFrame.top >= 0) {
                m_BackgroundFrame.top = -m_BackgroundHeight;
            }

            UpdateShoots();

            UpdateEnemies();

            UpdatePlayer();

            // Tenta adicionar um novo inimigo na tela:
            AddEnemy();

            //endregion

            //region Desenha os elementos na tela

            // Atualiza a interface de usuário:
            Main.setScore(Score);

            Canvas canvas = m_Holder.lockCanvas();

            canvas.save();

            // Define a cor de fundo:
            canvas.drawColor(Background);

            // Desenha a imagem de fundo:
            DrawBackground(canvas);

            // Desenha os tiros ativos:
            DrawShoots(canvas);

            // Desenha os inimigos:
            DrawEnemies(canvas);

            // Desenha o jogador:
            m_Player.Draw(canvas);

            // Desenha os controles:
            m_ControlLeft.Draw(canvas);
            m_ControlRight.Draw(canvas);
            m_ControlShoot.Draw(canvas);

            // Debug: mostra o FPS atual
            canvas.drawText(m_FrameCount + "", Margin, Margin, m_Paint);

            canvas.restore();

            m_Holder.unlockCanvasAndPost(canvas);

            //endregion
        }
    }

    private void UpdateShoots() {
        for (int i = 0; i < MAX_SHOOTS; i++) {
            Shoot shoot = m_Shoots[i];

            if (shoot == null || shoot.Hidden) continue;

            shoot.Position.y = shoot.Position.y - 20;

            if (shoot.Position.y <= -shoot.Size.y) {
                shoot.Hidden = true;
            }
        }
    }

    private void UpdateEnemies() {
        for (int i = 0; i < MAX_DOGOS; i++) {
            Enemy enemy = m_Enemies[i];

            if (enemy == null || enemy.Dead) continue;

            enemy.Position.y = enemy.Position.y + 15;

            if (enemy.Position.y >= Height) {
                enemy.Dead = true;
            }

            for (int j = 0; j < MAX_SHOOTS; j++) {
                Shoot shoot = m_Shoots[i];

                if (shoot == null || shoot.Hidden) continue;

                if (enemy.Hit(shoot.Position.x, shoot.Position.y)) {
                    enemy.Dead = true;

                    Score += 200;

                    break;
                }
            }
        }
    }

    private void UpdatePlayer() {
        if (m_ControlLeft.pressing && !m_ControlRight.pressing) {
            m_Player.Position.x = m_Player.Position.x - 20;
        }

        if (!m_ControlLeft.pressing && m_ControlRight.pressing) {
            m_Player.Position.x = m_Player.Position.x + 20;
        }

        if (m_Player.Position.x <= -m_Player.Size.x) {
            m_Player.Position.x = Width;
        } else if (m_Player.Position.x >= Width) {
            m_Player.Position.x = -m_Player.Size.x;
        }

        if (m_ControlShoot.pressing) {
            AddShoot();
        }
    }

    private void DrawBackground(Canvas canvas) {
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(m_BackgroundImage, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);

        paint.setShader(shader);

        canvas.drawRect(m_BackgroundFrame, paint);
    }

    private void DrawShoots(Canvas canvas) {
        for (int i = 0; i < MAX_SHOOTS; i++) {
            Sprite shoot = m_Shoots[i];

            if (shoot == null) continue;

            shoot.Draw(canvas);
        }
    }

    private void DrawEnemies(Canvas canvas) {
        for (int i = 0; i < MAX_DOGOS; i++) {
            Sprite enemy = m_Enemies[i];

            if (enemy == null) continue;

            enemy.Draw(canvas);
        }
    }

    private void AddShoot() {
        if (m_Time - SHOOT_INTERVAL < m_LastShootTime) return;

        for (int i = 0; i < MAX_SHOOTS; i++) {
            Shoot shoot = m_Shoots[i];

            if (shoot != null && !shoot.Hidden) continue;

            if (shoot == null) {
                shoot = new Shoot(m_ShootImage);

                m_Shoots[i] = shoot;
            }

            shoot.Position.x = m_Player.Position.x + (m_Player.Size.x - shoot.Size.x) / 2;
            shoot.Position.y = m_Player.Position.y;

            shoot.Hidden = false;

            m_LastShootTime = m_Time;

            break;
        }
    }

    private void AddEnemy() {
        if (m_Time - ENEMY_INTERVAL < m_LastEnemyTime) return;

        for (int i = 0; i < MAX_DOGOS; i++) {
            Enemy enemy = m_Enemies[i];

            if (enemy != null && !enemy.Dead) continue;

            if (enemy == null) {
                enemy = new Enemy(m_EnemyImage);

                m_Enemies[i] = enemy;
            }

            Random rnd = new Random();

            enemy.Position.x = rnd.nextFloat() * (Width - enemy.Size.x);
            enemy.Position.y = -enemy.Size.y;

            enemy.Dead = false;

            m_LastEnemyTime = m_Time;

            break;
        }
    }
}
