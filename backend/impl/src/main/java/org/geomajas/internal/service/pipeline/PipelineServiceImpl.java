/*
 * This is part of Geomajas, a GIS framework, http://www.geomajas.org/.
 *
 * Copyright 2008-2011 Geosparc nv, http://www.geosparc.com/, Belgium.
 *
 * The program is available in open source according to the GNU Affero
 * General Public License. All contributions in this program are covered
 * by the Geomajas Contributors License Agreement. For full licensing
 * details, see LICENSE.txt in the project root.
 */

package org.geomajas.internal.service.pipeline;

import org.geomajas.global.ExceptionCode;
import org.geomajas.global.GeomajasException;
import org.geomajas.service.pipeline.PipelineContext;
import org.geomajas.service.pipeline.PipelineHook;
import org.geomajas.service.pipeline.PipelineInfo;
import org.geomajas.service.pipeline.PipelineService;
import org.geomajas.service.pipeline.PipelineStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Service which is allows "executing" a pipeline.
 *
 * @param <RESPONSE> type of response object for the pipeline
 *
 * @author Joachim Van der Auwera
 */
@Component
public class PipelineServiceImpl<RESPONSE> implements PipelineService<RESPONSE> {

	@Autowired
	private ApplicationContext applicationContext;

	/** @inheritDoc */
	public void execute(String key, String layerId, PipelineContext context, RESPONSE response)
			throws GeomajasException {
		execute(getPipeline(key, layerId), context, response);
	}

	/** @inheritDoc */
	public void execute(PipelineInfo<RESPONSE> pipeline, PipelineContext startContext, RESPONSE response)
			throws GeomajasException {
		PipelineContext context = startContext;
		if (null == context) {
			context = createContext();
		}
		for (PipelineStep<RESPONSE> step : pipeline.getPipeline()) {
			if (context.isFinished()) {
				break;
			}
			step.execute(context, response);
		}
	}

	/** @inheritDoc */
	public PipelineInfo<RESPONSE> getPipeline(String pipelineName, String layerId) throws GeomajasException {
		PipelineInfo<RESPONSE> layerPipeline = null;
		PipelineInfo<RESPONSE> defaultPipeline = null;
		Collection<PipelineInfo> pipelines = applicationContext.getBeansOfType(PipelineInfo.class).values();
		for (PipelineInfo<RESPONSE> pipeline : pipelines) {
			if (pipeline.getPipelineName().equals(pipelineName)) {
				String lid = pipeline.getLayerId();
				if (null == lid) {
					defaultPipeline = pipeline;
				} else if (lid.equals(layerId)) {
					layerPipeline = pipeline;
				}
			}
		}
		if (null == layerPipeline) {
			layerPipeline = defaultPipeline;
		}
		if (null == layerPipeline) {
			throw new GeomajasException(ExceptionCode.PIPELINE_UNKNOWN, pipelineName, layerId);
		}
		extendPipeline(layerPipeline);
		return layerPipeline;
	}

	private void extendPipeline(PipelineInfo pipeline) throws GeomajasException {
		if (null == pipeline.getPipeline() && null != pipeline.getDelegatePipeline()) {
			PipelineInfo delegate = pipeline.getDelegatePipeline();
			extendPipeline(delegate);
			
			Map<String, PipelineStep<RESPONSE>> extensions = pipeline.getExtensions();
			List<PipelineStep<RESPONSE>> steps;
			if (null == extensions) {
				steps = delegate.getPipeline();
			} else {
				steps = new ArrayList<PipelineStep<RESPONSE>>(delegate.getPipeline());
				int count = 0;
				for (int i = steps.size() - 1 ; i >= 0 ; i--) {
					PipelineStep<RESPONSE> step = steps.get(i);
					if (step instanceof PipelineHook) {
						PipelineStep<RESPONSE> ext = extensions.get(step.getId());
						if (null != ext) {
							steps.add(i + 1, ext);
							count++;
						}
					}
				}
				if (count != extensions.size()) {
					throw new GeomajasException(ExceptionCode.PIPELINE_UNSATISFIED_EXTENSION,
							pipeline.getPipelineName(), pipeline.getLayerId());
				}
			}
			pipeline.setPipeline(steps);
		}
	}

	/** @inheritDoc */
	public PipelineContext createContext() {
		return new PipelineContextImpl();
	}
}
