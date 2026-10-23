package com.renova.renova.Pck_Persistencia;

import com.renova.renova.Pck_DAO.ConexaoMySql;
import com.renova.renova.Pck_Model.Pck_Entities.UsuarioModel;

import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioPersistencia {

    Connection conn = null;
    CallableStatement stmt = null;
    boolean sucesso = false;

    public UsuarioModel login (UsuarioModel usuario){
        String sql = "{CALL PROC_LOGIN_USUARIO(?, ?)}";
        Connection conn = null;
        CallableStatement stmt = null;
        ResultSet result = null;

        try{
            ConexaoMySql conexaoBD = new ConexaoMySql();
            conn = ConexaoMySql.getConn(ConexaoMySql.login, ConexaoMySql.senha);
            stmt = conn.prepareCall(sql);

            stmt.setString(1, usuario.getEmail());
            stmt.setString(2, usuario.getSenha());

            result = stmt.executeQuery();

            if(result.next()){
                try{
                    usuario.setIdUsuario(result.getInt("id_usuario"));
                    usuario.setNome(result.getString("nome"));
                    usuario.setTipoUsuario(result.getString("tipo_usuario"));

                    System.out.println("Login feito.");

                    return usuario;

                } catch (Exception e) {
                    throw new RuntimeException("Erro ao atribuir valores ao objeto usuário após login: " + e);
                }

            } else{
                System.out.println("Erro ao logar.");
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao conectar com o banco de dados no login: " + e.getMessage());
        } finally {
            try {
                if (result != null) result.close();
                if (stmt != null) stmt.close();
                if (conn != null) new ConexaoMySql().desconectar();
            } catch (SQLException e) {
                System.out.println("Aviso: Falha ao fechar conexão - " + e.getMessage());
            }
        }

    }

    public void CadastroUsuario(UsuarioModel usuario) throws Exception{

        String sql = "{CALL PROC_CADASTRAR_USUARIO(?, ?, ?, ?, ?)}";

        try{
            ConexaoMySql conexaoBD = new ConexaoMySql();

            conn = ConexaoMySql.getConn(ConexaoMySql.login, ConexaoMySql.senha);

            stmt = conn.prepareCall(sql);

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getTelefone());
            stmt.setString(3, usuario.getEmail());
            stmt.setString(4, usuario.getSenha());
            stmt.setString(5, usuario.getTipoUsuario());

            int linhasAfetadas = stmt.executeUpdate();

            if(linhasAfetadas > 0){
                System.out.println("Novo usuario cadastrado com sucesso.");
            } else{
                System.out.println("Erro ao cadastrar usuario.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro no banco de dados ao cadastrar usuário: " + e.getMessage());

        }finally {
            try {
                if (stmt != null){
                    stmt.close();
                }
                if (conn != null){
                    new ConexaoMySql().desconectar();
                }
            } catch (SQLException e) {
                System.out.println("Falha ao fechar a conexão com o banco - " + e.getMessage());

            }
        }

    }

}
