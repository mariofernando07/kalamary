/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.configuracion;

import entities.CfgAplicaciondocumento;
import entities.CfgDocumento;
import entities.CfgEmpresa;
import entities.CfgEmpresasede;
import entities.CfgTipoempresa;
import entities.SegUsuario;
import facades.CfgAplicaciondocumentoFacade;
import facades.CfgDocumentoFacade;
import facades.CfgEmpresaFacade;
import facades.CfgEmpresasedeFacade;
import facades.CfgTipoempresaFacade;
import facades.FacDocumentosmasterFacade;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.ejb.EJB;
import java.util.List;
import org.primefaces.context.RequestContext;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import javax.faces.application.FacesMessage;
import java.util.Date;
import managedBeans.seguridad.SesionMB;
import javax.annotation.PostConstruct;

/**
 *
 * @author mario
 */
@ManagedBean
@SessionScoped
public class DocumentoMB implements Serializable {

    private String codigoDocumento;
    private int tipoEmpresa;
    private String nombreDocumento;
    private String abreviatura;
    private String prefijo;
    private int aplicacion;
    private boolean activo;
    private int rangoInicial;
    private int rangoFinal;
    private int numActual;
    private String resDian;
    private String observacion;

    private SesionMB sesionMB;
    private boolean aplicacionValidada;

    private List<CfgEmpresasede> listaSede;
    private List<CfgDocumento> listaDocumento;
    private List<CfgAplicaciondocumento> listaAplicacionDocumento;

    private CfgEmpresa empresaSeleccionada;
    private CfgEmpresasede sedeSeleccionada;
    private CfgDocumento documentoSeleccionado;
    private SegUsuario usuarioActual;

    @EJB
    CfgEmpresaFacade empresaFacade;

    @EJB
    CfgEmpresasedeFacade sedeFacade;

    @EJB
    CfgDocumentoFacade documentoFacade;

    @EJB
    CfgTipoempresaFacade tipoempresaFacade;

    @EJB
    CfgAplicaciondocumentoFacade aplicaciondocumentoFacade;

    @EJB
    FacDocumentosmasterFacade documentosmasterFacade;

    public DocumentoMB() {
    }

    @PostConstruct
    private void init() {
        FacesContext context = FacesContext.getCurrentInstance();
        sesionMB = context.getApplication().evaluateExpressionGet(context, "#{sesionMB}", SesionMB.class);
        listaAplicacionDocumento = aplicaciondocumentoFacade.findAll();
        usuarioActual = sesionMB.getUsuarioActual();
        sedeSeleccionada = sesionMB.getSedeActual();
        empresaSeleccionada = sesionMB.getEmpresaActual();
        rangoInicial = 1;
        activo = true;
    }

    public void cargarListaSedes() {
        if (empresaSeleccionada != null) {
            listaSede = sedeFacade.buscarSedesPorEmpresa(empresaSeleccionada.getIdEmpresa());
            RequestContext.getCurrentInstance().update("FormModalSede");
            RequestContext.getCurrentInstance().execute("PF('dlgSede').show()");
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Informacion", "Determine la empresa"));
        }
    }

    public void buscarDocumento() {
        if (sedeSeleccionada != null) {
            if (!codigoDocumento.trim().isEmpty()) {
                documentoSeleccionado = documentoFacade.buscarDocumentoPorSedeAndCodigo(sedeSeleccionada, codigoDocumento);
                cargarInformacionDocumento();
            } else {
                RequestContext.getCurrentInstance().update("IdFormDocumento");
            }
        } else {
            limpiarInformacionDocumento();
        }
        RequestContext.getCurrentInstance().update("IdFormDocumento");
    }

    public void cargarListaDocumento() {
        if (sedeSeleccionada != null) {
            listaDocumento = documentoFacade.buscarDocumentoPorSede(sedeSeleccionada);
            RequestContext.getCurrentInstance().update("FormModalDocumento");
            RequestContext.getCurrentInstance().execute("PF('dlgDocumento').show()");
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Informacion", "Determine la sede"));
        }

    }

    public void cargarInformacionDocumento() {
        if (documentoSeleccionado != null) {
            setSedeSeleccionada(documentoSeleccionado.getCfgempresasedeidSede());
            setCodigoDocumento(documentoSeleccionado.getCodDocumento());
            setTipoEmpresa(documentoSeleccionado.getCfgTipoempresaId().getId());
            setNombreDocumento(documentoSeleccionado.getNomDoc());
            setAbreviatura(documentoSeleccionado.getAbreviatura());
            setPrefijo(documentoSeleccionado.getPrefijoDoc());
            if (documentoSeleccionado.getCfgAplicaciondocumentoIdaplicacion() != null) {
                setAplicacion(documentoSeleccionado.getCfgAplicaciondocumentoIdaplicacion().getIdaplicacion());
            }
            setActivo(documentoSeleccionado.getActivo());
            setRangoInicial(documentoSeleccionado.getIniDocumento());
            setRangoFinal(documentoSeleccionado.getFinDocumento());
            setNumActual(documentoSeleccionado.getActDocumento());
            setResDian(documentoSeleccionado.getResDian());
            setObservacion(documentoSeleccionado.getObsDocumento());
        }
        RequestContext.getCurrentInstance().update("IdFormDocumento");
    }

    private void limpiarInformacionDocumento() {
//        setCodigoDocumento(null);
        setTipoEmpresa(0);
        setNombreDocumento(null);
        setAbreviatura(null);
        setPrefijo(null);
        setActivo(true);
        setRangoInicial(1);
        setRangoFinal(0);
        setNumActual(0);
        setResDian(null);
        setAplicacion(0);
        setObservacion(null);
    }

    private void limpiarFormulario() {
        setCodigoDocumento(null);
        documentoSeleccionado = null;
        limpiarInformacionDocumento();
    }

    public void cancelar() {
        limpiarFormulario();
        RequestContext.getCurrentInstance().update("IdFormDocumento");
    }

    public void validarAplicacion() {
        aplicacionValidada = true;
        if (sedeSeleccionada != null && aplicacion != 0) {
            CfgDocumento cfgDocumento = documentoFacade.buscarAplicacionDocumentoPorSede(sedeSeleccionada, aplicacion);
            if (cfgDocumento != null) {
                if (documentoSeleccionado != null) {
                    if (!documentoSeleccionado.equals(cfgDocumento)) {
                        aplicacionValidada = false;
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Aplicacion ya implementada por el documento " + cfgDocumento.getNomDoc()));
                    }
                }
            }
        }
    }

    public void accion() {
        if (documentoSeleccionado != null) {
            editarDocumento();
        } else {
            crearDocumento();
        }
    }

    private boolean validacion() {
        boolean ban = true;
        if (sedeSeleccionada == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Especifique la sede"));
            return false;
        }
//        solo los usuarios super y admin pueden modificar y crear documentos
        if (!usuarioActual.getCfgRolIdrol().getCodrol().equals("00001") && !usuarioActual.getCfgRolIdrol().getCodrol().equals("00002")) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No tiene permisos para crear documentos"));
            return false;
        }
        if (codigoDocumento.trim().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Falta codigo del documento"));
            return false;
        }
        if (nombreDocumento.trim().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Nombre Documento vacio"));
            return false;
        }
        if (abreviatura.trim().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Abreviatura vacia"));
            return false;
        }
        if (prefijo.trim().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Prefijo vacio"));
            return false;
        }
        validarAplicacion();
        if (!aplicacionValidada) {
            return false;
        }
        if (rangoInicial <= 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "rango inicial no valido"));
            return false;
        }
        if (rangoFinal <= 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "rango final no valido"));
            return false;
        }
        if (rangoFinal < rangoInicial || rangoInicial > rangoFinal) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "rango inicial y final no valido"));
            return false;
        }
        return ban;

    }

    private void crearDocumento() {
        if (!validacion()) {
            return;
        }
        try {
            CfgDocumento documento = new CfgDocumento();
            documento.setCfgempresasedeidSede(sedeSeleccionada);
            documento.setCodDocumento(codigoDocumento.trim().toUpperCase());
//            CfgTipoempresa tipoempresa = tipoempresaFacade.find(tipoEmpresa);
//            documento.setCfgTipoempresaId(tipoempresa);
            documento.setCfgTipoempresaId(empresaSeleccionada.getCfgTipoempresaId());
            documento.setNomDoc(nombreDocumento.trim().toUpperCase());
            documento.setAbreviatura(abreviatura.trim().toUpperCase());
            documento.setPrefijoDoc(prefijo.trim().toUpperCase());
            documento.setActivo(activo);
            documento.setIniDocumento(rangoInicial);
            documento.setFinDocumento(rangoFinal);
            if (aplicacion != 0) {
                documento.setCfgAplicaciondocumentoIdaplicacion(aplicaciondocumentoFacade.find(aplicacion));
            }
            documento.setActDocumento(0);
            documento.setResDian(resDian);
            documento.setObsDocumento(observacion);
            documento.setFecCrea(new Date());
            documento.setSegusuarioidUsuario(usuarioActual);
            documento.setFinalizado(false);
            documentoFacade.create(documento);
            limpiarFormulario();
            RequestContext.getCurrentInstance().update("IdFormDocumento");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Documento creado"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Documento No creado"));
        }
    }

    private void editarDocumento() {
        if (!validacion()) {
            return;
        }
        if (documentoSeleccionado == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Elija un documento"));
            return;
        }
        if (documentoSeleccionado.getFinalizado()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Informacion", "El documento se encuentra finalizado. Debe crear uno nuevo"));
            return;
        }
        int aplicacionActual = documentoSeleccionado.getCfgAplicaciondocumentoIdaplicacion() == null ? 0 : documentoSeleccionado.getCfgAplicaciondocumentoIdaplicacion().getIdaplicacion();
        int rangoIncialActual = documentoSeleccionado.getIniDocumento();
        int rangoFinalActual = documentoSeleccionado.getFinDocumento();
        int totalCreados = documentosmasterFacade.totalDocumentosPorAplicacionAndRango(documentoSeleccionado.getIdDoc(), rangoInicial, rangoFinal);
        if (aplicacionActual != aplicacion || (rangoIncialActual != rangoInicial || rangoFinalActual != rangoFinal)) {
            //SE VALIDA QUE EL RANGO ESTE LIBRE. NO HAY DOCUKENTOSMASTER CREADOS
            if (totalCreados > 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Informacion", "Ya existen elementos creados dentro del rango"));
                return;
            }
            documentoSeleccionado.setActDocumento(0);
        } 
        try {
            CfgTipoempresa tipoempresa = tipoempresaFacade.find(tipoEmpresa);
            documentoSeleccionado.setCfgTipoempresaId(tipoempresa);
            documentoSeleccionado.setNomDoc(nombreDocumento.trim().toUpperCase());
            documentoSeleccionado.setAbreviatura(abreviatura.trim().toUpperCase());
            documentoSeleccionado.setPrefijoDoc(prefijo.trim().toUpperCase());
            if (aplicacion != 0) {
                documentoSeleccionado.setCfgAplicaciondocumentoIdaplicacion(aplicaciondocumentoFacade.find(aplicacion));
            }
            documentoSeleccionado.setActivo(activo);
            documentoSeleccionado.setIniDocumento(rangoInicial);
            documentoSeleccionado.setFinDocumento(rangoFinal);
            documentoSeleccionado.setResDian(resDian);
            documentoSeleccionado.setObsDocumento(observacion);
            documentoFacade.edit(documentoSeleccionado);
            limpiarFormulario();
            RequestContext.getCurrentInstance().update("IdFormDocumento");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Documento actualizado"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Documento No actualizado"));
        }
    }

    public int getTipoEmpresa() {
        return tipoEmpresa;
    }

    public void setTipoEmpresa(int tipoEmpresa) {
        this.tipoEmpresa = tipoEmpresa;
    }

    public String getNombreDocumento() {
        return nombreDocumento;
    }

    public void setNombreDocumento(String nombreDocumento) {
        this.nombreDocumento = nombreDocumento;
    }

    public String getAbreviatura() {
        return abreviatura;
    }

    public void setAbreviatura(String abreviatura) {
        this.abreviatura = abreviatura;
    }

    public String getPrefijo() {
        return prefijo;
    }

    public void setPrefijo(String prefijo) {
        this.prefijo = prefijo;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public int getRangoInicial() {
        return rangoInicial;
    }

    public void setRangoInicial(int rangoInicial) {
        this.rangoInicial = rangoInicial;
    }

    public int getRangoFinal() {
        return rangoFinal;
    }

    public void setRangoFinal(int rangoFinal) {
        this.rangoFinal = rangoFinal;
    }

    public int getNumActual() {
        return numActual;
    }

    public void setNumActual(int numActual) {
        this.numActual = numActual;
    }

    public String getResDian() {
        return resDian;
    }

    public void setResDian(String resDian) {
        this.resDian = resDian;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public CfgEmpresasede getSedeSeleccionada() {
        return sedeSeleccionada;
    }

    public void setSedeSeleccionada(CfgEmpresasede sedeSeleccionada) {
        this.sedeSeleccionada = sedeSeleccionada;
    }

    public String getCodigoDocumento() {
        return codigoDocumento;
    }

    public void setCodigoDocumento(String codigoDocumento) {
        this.codigoDocumento = codigoDocumento;
    }

    public CfgDocumento getDocumentoSeleccionado() {
        return documentoSeleccionado;
    }

    public void setDocumentoSeleccionado(CfgDocumento documentoSeleccionado) {
        this.documentoSeleccionado = documentoSeleccionado;
    }

    public List<CfgEmpresasede> getListaSede() {
        return listaSede;
    }

    public void setListaSede(List<CfgEmpresasede> listaSede) {
        this.listaSede = listaSede;
    }

    public List<CfgDocumento> getListaDocumento() {
        return listaDocumento;
    }

    public void setListaDocumento(List<CfgDocumento> listaDocumento) {
        this.listaDocumento = listaDocumento;
    }

    public List<CfgAplicaciondocumento> getListaAplicacionDocumento() {
        return listaAplicacionDocumento;
    }

    public void setListaAplicacionDocumento(List<CfgAplicaciondocumento> listaAplicacionDocumento) {
        this.listaAplicacionDocumento = listaAplicacionDocumento;
    }

    public int getAplicacion() {
        return aplicacion;
    }

    public void setAplicacion(int aplicacion) {
        this.aplicacion = aplicacion;
    }

}
